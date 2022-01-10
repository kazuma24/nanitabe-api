package com.fact.nanitabe.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fact.nanitabe.application.resource.response.Shop;
import com.fact.nanitabe.application.resource.response.ShopListResponse;
import com.fact.nanitabe.common.constants.NanitabeConstants;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.common.exception.AuthenticationFailureException;
import com.fact.nanitabe.common.exception.DuplicateException;
import com.fact.nanitabe.common.logic.HotPepparCommonLogic;
import com.fact.nanitabe.common.logic.JwtCommonLogic;
import com.fact.nanitabe.domain.logic.LocationInfoSearchLogic;
import com.fact.nanitabe.infrastructure.entity.Favos;
import com.fact.nanitabe.infrastructure.entity.Users;
import com.fact.nanitabe.infrastructure.repository.FavosRepository;
import com.fact.nanitabe.infrastructure.repository.UsersRepository;

/**
 * nanitabeサービスクラス
 * 
 * @author k-sato
 *
 */
@Service
public class NanitabeServiceImpl implements NanitabeService {

	@Autowired
	UsersRepository usersRepository;
	
	@Autowired
	FavosRepository favosRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	JwtCommonLogic jwtCommonLogic;

	@Autowired
	HotPepparCommonLogic hpCommonLogic;
	
	@Autowired
	LocationInfoSearchLogic locationInfoSearchLogic;
	
	private static final Logger logger = LogManager.getLogger(NanitabeServiceImpl.class);

	/**
	 * Emailの重複チェックメソッド(true= 重複している : false=重複してない)
	 * @param email メールアドレス
	 * @return true = 重複 false = 重複なし
	 */
	@Override
	public boolean isDuplicateEmail(String email) throws Exception {
		// メール重複チェック
		try {
			Users user = usersRepository.findByEmail(email);
			if (user == null) {
				return false;
			}
			return true;
		} catch (IncorrectResultSizeDataAccessException e) {
			// (ありえないと思うが)複数あった場合
			logger.error(NanitabeLogConstants.USERS_TRANZACTION_SELECT_ERROR_2, e);
			throw e;
		} catch (Exception e) {
			logger.error(NanitabeLogConstants.USERS_TRANZACTION_SELECT_ERROR_1, e);
			throw e;
		}
	}

	/**
	 * 新規登録処理メソッド
	 * @param email メールアドレス
	 * @param password パスワード
	 * @return 発行したJWTトークン
	 * @throws	Exception
	 * */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public String newRegistration(String email, String password) throws Exception {

		// ログ
		logger.info(NanitabeLogConstants.NEWREGISTER_INFO_2);

		// Entity作成
		Users user = new Users();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));

		Users registedUser;
		try {
			// DB登録
			registedUser = usersRepository.save(user);
		} catch (Exception e) {
			logger.error(NanitabeLogConstants.USERS_TRANZACTION_INSERT_ERROR_1, e);
			throw e;
		}
		// JWTトークン発行
		String jwtToken = jwtCommonLogic.generateJwtToken(registedUser.getUserId());
		return jwtToken;
	}

	/**
	 * 現在地検索API処理メソッド
	 * @param lat 経度
	 * @param lng 緯度
	 * @return ホットペッパーAPIレスポンス
	 * @throws Exception
	 */
	@Override
	public ShopListResponse tryGetShop(String lat, String lng) throws Exception {

		// パラメーターマップを作成
		Map<String, String> paramSet = new HashMap<>();
		paramSet.put("lat", lat);
		paramSet.put("lng", lng);
		paramSet.put("count", NanitabeConstants.QUERY_COUNT_PARAM);

		// エンドポイント取得(グルメサーチAPI）
		String endPoint = hpCommonLogic.getGourmandSearchApiEndPoint();
		// 現在地から検索用のURL作成
		String reqUrl = hpCommonLogic.generateAddParamUrl(paramSet, endPoint);

		// ホットペッパーAPIたたく
		String responseBody = locationInfoSearchLogic.fetchShop(reqUrl);

		// レスポンうようにPOJO作成
		ShopListResponse response = hpCommonLogic.createResponse(responseBody);

		return response;
	}

	/**
	 * ログイン(JWT)あり処理メソッド
	 * 
	 * @param jwtToken JWTトークン
	 * @param email    メールアドレス
	 * @param password パスワード
	 * @throws JWTVerificationException　JWTトークン無効
	 * @throws AuthenticationFailureException　認証失敗
	 * @throws Exception　サーバーエラー
	 * @return 成功時:新しいJWT、失敗時例外
	 */
	@Override
	public String tryLoginJwtExists(String jwtToken, String email, String password)
			throws JWTVerificationException, AuthenticationFailureException, Exception {

		// JWTの検証
		String jwt = jwtCommonLogic.verifyJWT(jwtToken);
		// JWTからuserId取得
		Integer userId = jwtCommonLogic.getUserId(jwt);
		
		// userIdでDB検索
		Optional<Users> user;
		try {
			user = usersRepository.findById(userId);
		}catch(Exception e) {
			// DBエラー
			logger.error(NanitabeLogConstants.LOGIN_AUTH_ERROR_3, e.getMessage());
			throw e;
		}

		if (user.isPresent()) {
			Users registedUser = user.get();
			// JWTトークンのuserIdで取得した、email,passwordをリクエスト値と比較
			if (email.equals(registedUser.getEmail())
					&& passwordEncoder.matches(password, registedUser.getPassword())) {
				// ログイン中のユーザーのため、新しいJWTトークンを発行して返却
				String newJwtToken = jwtCommonLogic.generateJwtToken(userId);
				return newJwtToken;
			} else {
				// パスワード不一致、認証失敗
				logger.error(NanitabeLogConstants.LOGIN_AUHT_ERROR_1, email);
				throw new AuthenticationFailureException();
			}
		} else {
			// トークンのuserIdでDBにデータがない場合 不正トークンとする
			logger.error(NanitabeLogConstants.LOGIN_AUHT_ERROR_2, userId);
			throw new JWTVerificationException(NanitabeMessageConstants.LOGIN_ACCESS_DENIED);
		}
	}

	/**
	 * ログイン(JWT)なし処理メソッド
	 * 
	 * @param email    メールアドレス
	 * @param password パスワード
	 * @throws AuthenticationFailureException 認証失敗
	 * @throws Exception　サーバーエラー
	 * @return 成功時:新しいJWT、失敗時例外
	 */
	@Override
	public String tryLogin(String email, String password) throws AuthenticationFailureException, Exception {

		Users registedUser = null;
		try {
			// emailをもとにDB検索
			registedUser = usersRepository.findByEmail(email);
		} catch (Exception e) {
			// DBエラー
			logger.error(NanitabeLogConstants.LOGIN_AUTH_ERROR_3, e.getMessage());
			throw e;
		}

		if (registedUser == null) {
			// 認証失敗、emailでDBにデータなし
			logger.error(NanitabeLogConstants.LOGIN_AUHT_ERROR_4, email);
			throw new AuthenticationFailureException();
		} else {
			if (email.equals(registedUser.getEmail())
					&& passwordEncoder.matches(password, registedUser.getPassword())) {
				// ログイン成功、新しいJWTトークンを発行して返却
				logger.info(NanitabeLogConstants.LOGIN_INFO_1);
				String newJwtToken = jwtCommonLogic.generateJwtToken(registedUser.getUserId());
				return newJwtToken;
			} else {
				// パスワード不一致、認証失敗
				logger.error(NanitabeLogConstants.LOGIN_AUHT_ERROR_1, email);
				throw new AuthenticationFailureException();
			}
		}
	}
	
	/**
	 * キーワード、エリア、ジャンル、予算の各コードの有効性をチェックします
	 * true = 無効 false = 有効
	 */
	@Override
	public boolean isInValidAll(String keyWord, String areaCode, String genre, String budget) {
		
		// キーワード
		if(StringUtils.isNoneEmpty(keyWord)) {
			if(hpCommonLogic.isInValidKeyWord(keyWord)) {
				return true;
			}
		}
		// エリアコード
		if(StringUtils.isNotEmpty(areaCode)) {
			if(hpCommonLogic.isInValidAreaCode(areaCode)) {
				return true;
			}
		}
		
		// ジャンルコード
		if(StringUtils.isNotEmpty(genre)) {
			if(hpCommonLogic.isInValidGenreCode(genre)) {
				return true;
			}
		}
		// 予算コード
		if(StringUtils.isNotEmpty(budget)) {
			if(hpCommonLogic.isInValidBudgetCode(budget)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 詳細検索処理メソッド
	 * @param String[] 0:keyWord 1:genre 2:budget 3areaCode 4:lat 5:lng
	 * @return ホットペッパーAPIレスポンス
	 * @throws Exception
	 */
	@Override
	public ShopListResponse tryDetailedSearch(String...params) throws Exception {
		
		// パラメーターマップを作成
		Map<String, String> paramSet = new HashMap<>();
		paramSet.put("count", NanitabeConstants.QUERY_COUNT_PARAM);
		if(StringUtils.isNotEmpty(params[0])) {
			paramSet.put("keyWord", params[0]);
		}
		if(StringUtils.isNotEmpty(params[1])) {
			paramSet.put("genre", params[1]);
		}
		if(StringUtils.isNotEmpty(params[2])) {
			paramSet.put("budget", params[2]);
		}
		if(params.length == 4) {
			paramSet.put("areaCode", params[3]);
		} else {
			paramSet.put("lat", params[3]);
			paramSet.put("lng", params[4]);
		}
		
		// エンドポイント取得(グルメサーチAPI）
		String endPoint = hpCommonLogic.getGourmandSearchApiEndPoint();
		
		// リクエストURL作成
		String reqUrl = hpCommonLogic.generateAddParamUrl(paramSet, endPoint);
		
		// ホットペッパーAPIたたく
		String responseBody = locationInfoSearchLogic.fetchShop(reqUrl);

		// レスポンス作成
		ShopListResponse response = hpCommonLogic.createResponse(responseBody);
		
		return response;
	}

	/**
	 * お気に入り登録処理メソッド
	 * @param jwtToken JWTトークン
	 * @param storeId お店ID
	 * @return お店の掲載店名
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public String tryFavoriteRegiste(String jwtToken, String storeId) throws DuplicateException, Exception {
		
		//　スキーマ抜き
		String jwt = jwtCommonLogic.withoutSchema(jwtToken);
		// JWTからuserId取得
	    Integer userId = jwtCommonLogic.getUserId(jwt);
	    // お気に入り済みかチェック
    	int count = favosRepository.countFavo(userId, storeId);
    	if(count >= 1) {
    		// 登録済み
    		throw new DuplicateException();
    	}
	    // パラメーター作成
	    Map<String, String> paramSet = new HashMap<>();
	    paramSet.put("id", storeId);
	    paramSet.put("count", NanitabeConstants.QUERY_COUNT_PARAM);
	    // エンドポイント取得(グルメサーチAPI）
	    String endPoint = hpCommonLogic.getGourmandSearchApiEndPoint();
	    // リクエストURL作成
	    String reqUrl = hpCommonLogic.generateAddParamUrl(paramSet, endPoint);
	    // ホットペッパーAPIたたく
	    String responseBody = hpCommonLogic.fetch(reqUrl);
	    // レスポンスを解析
	    ShopListResponse response = hpCommonLogic.createResponse(responseBody);
	    // お気に入り店舗情報セット
	    Favos favos = new Favos();
	    favos.setUserId(userId);
	    favos.setStoreId(response.getShop().get(0).getId());
	    favos.setName(response.getShop().get(0).getName());
	    favos.setPhotoPc(response.getShop().get(0).getPhotoPc());
	    favos.setPhotoMobile(response.getShop().get(0).getPhotoMobile());
	    favos.setAccess(response.getShop().get(0).getAccess());
	    favos.setUrl(response.getShop().get(0).getUrl());
	    favos.setGenreName(response.getShop().get(0).getGenreName());
	    favos.setAddress(response.getShop().get(0).getAddress());
	    favos.setCatchPhrase(response.getShop().get(0).getCatchPhrase());
	    favos.setBudgetAverage(response.getShop().get(0).getBudgetAverage());
	    favos.setOpen(response.getShop().get(0).getOpen());
	    favos.setClose(response.getShop().get(0).getClose());
	    
	    try {
	    	// DB登録
	    	favosRepository.save(favos);
	    }catch(Exception e) {
	    	logger.error(NanitabeLogConstants.FAVOS_TRANZACTION_INSERT_ERROR, e.getMessage());
	    	throw e;
	    }
	    
	    // お店の名前返却
		return response.getShop().get(0).getName();
	}

	/**
	 * お気に入り削除処理メソッド
	 * 
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void tryFavoriteDelete(String jwtToken, String storeId) throws Exception {
		//　スキーマ抜き
		String jwt = jwtCommonLogic.withoutSchema(jwtToken);
		// JWTからuserId取得
	    Integer userId = jwtCommonLogic.getUserId(jwt);
	    
	    //　お気に入り削除
	    try {
	    	favosRepository.deleteByUserIdAndStoreId(userId, storeId);
	    }catch(Exception e) {
	    	logger.error(NanitabeLogConstants.FAVOS_TRANZACTION_DELETE_ERROR, e.getMessage());
	    	throw e;
	    }
		
	}
	
	/**
	 * 小エリアマスタAPI処理メソッド
	 * @param middleArea 中エリアコード
	 * @return 小エリアマスタAPIjsonレスポンス
	 * @throws Exception 
	 */
	@Override
	public String trySmallAreaApi(String middleArea) throws Exception {
		
		// エリアコードバリデーション
		if(hpCommonLogic.isInValidAreaCode(middleArea)) {
			return "badAreaCode";
		}
		//　エンドポイント取得(小エリアマスタ）
		String endPoint = hpCommonLogic.getSmallAreaApiEndPoint();
		//　パラメーラーセット
		Map<String, String> paramSet = new HashMap<>();
		paramSet.put("areaCode", middleArea);
		// リクエストURL取得
		String reqUrl = hpCommonLogic.generateAddParamUrl(paramSet, endPoint);
		// ホットペッパーAPIたたく
		String responseBody = hpCommonLogic.fetch(reqUrl);
		// json文字列をjsonオブジェクトへ変換
		try {
			JSONObject jsonObject = new JSONObject(responseBody);
			return jsonObject.toString();
		}catch (Exception e) {
			// TODO　ログ
            System.out.println("Exception : "+e.toString());
            throw e;
        }
	}

	/**
	 * お気に入り一覧取得API処理メソッド
	 */
	@Override
	public ShopListResponse tryFavoriteGet(String jwtToken) throws Exception {
		// レスポンス用
		ShopListResponse res = new ShopListResponse();
		// スキーマ抜きjwt取得
		String jwt = jwtCommonLogic.withoutSchema(jwtToken);
		// userID取得
		Integer userId = jwtCommonLogic.getUserId(jwt);
		// userIdにひもつくお気に入りリスト取得
		List<Favos> favos = null;
		try {
			 favos = favosRepository.getFavoList(userId);
		}catch(Exception e) {
			// TODO　ログ
			throw e;
		}
		// レスポンス用に整形
		List<Shop> shopList = new ArrayList<>();
		String numOfFavo = String.valueOf(favos.size());
		if(favos != null && favos.size() > 0) {
			res.setResultsAvailable(numOfFavo); // お気に入り数
			res.setMessage("お気に入りを取得しました");
			res.setErrorCode("0");
			for(Favos f : favos) {
				Shop shop = new Shop();
				shop.setAccess(f.getAccess());
				shop.setAddress(f.getAddress());
				shop.setBudgetAverage(f.getBudgetAverage());
				shop.setCatchPhrase(f.getCatchPhrase());
				shop.setClose(f.getClose());
				shop.setGenreName(f.getGenreName());
				shop.setId(f.getStoreId());
				shop.setName(f.getName());
				shop.setOpen(f.getOpen());
				shop.setPhotoMobile(f.getPhotoMobile());
				shop.setPhotoPc(f.getPhotoPc());
				shop.setUrl(f.getUrl());
				shopList.add(shop);
			}
			res.setShop(shopList);
		} else {
			res.setResultsAvailable(numOfFavo); // お気に入り数
			res.setMessage("お気に入りはありません");
			res.setErrorCode("0");
			res.setShop(shopList);
		}
		return res;
	}

	
}
