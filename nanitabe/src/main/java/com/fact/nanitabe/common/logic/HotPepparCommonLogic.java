package com.fact.nanitabe.common.logic;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fact.nanitabe.application.resource.response.Shop;
import com.fact.nanitabe.application.resource.response.ShopListResponse;
import com.fact.nanitabe.common.constants.NanitabeConstants;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.common.property.HotPepparProperties;
import com.fact.nanitabe.common.property.MailProperties;
import com.fact.nanitabe.domain.client.HotPepparClient;
import com.fact.nanitabe.domain.event.MailSendEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HotPepparAPI関連の共通機能クラス
 * 
 * @author k-sato
 */
@Component
public class HotPepparCommonLogic {

	@Autowired
	HotPepparProperties hotPepparProperties;
	
	@Autowired
	MailSendEventPublisher mailSendPublisher;
	
	@Autowired
	MailProperties mailProperties;
	
	@Autowired
	HotPepparClient client;
	
	@Autowired
	ObjectMapper mapper;

	private static final Logger logger = LogManager.getLogger(HotPepparCommonLogic.class);

	/**
	 * グルメサーチAPIエンドポイントを返却
	 * @return
	 */
	public String getGourmandSearchApiEndPoint() {
		return  hotPepparProperties.getGourmandSearchApi();
	}
	
	/**
	 * 小エリアマスタAPIエンドポイントを返却
	 * @return
	 */
	public String getSmallAreaApiEndPoint() {
		return hotPepparProperties.getSmallAreaApiUrl();
	}
		
	/**
	 * APIエンドポイントに各パラメーターをセットするメソッド
	 * @param paramSet
	 * @return
	 */
	public String generateAddParamUrl(Map<String, String> paramSet, String endPoint) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(endPoint);
		
		// APIキー取得
		String apiKey = hotPepparProperties.getApiKey();
		// key設定
		sb.append(NanitabeConstants.KEY_QUERY_PREFIX);
		sb.append(apiKey);
		// format設定
		sb.append(NanitabeConstants.FORMAT_QUERY_PREFIX);
		sb.append(NanitabeConstants.QUERY_FORMAT_PARAM);
		
		// count
		if(paramSet.containsKey("count")) {
			sb.append(NanitabeConstants.COUNT_QUERY_PREFIX);
			sb.append(paramSet.get("count"));
		}
		// お店ID
		if(paramSet.containsKey("id")) {
			sb.append(NanitabeConstants.ID_QUERY_PREFIX);
			sb.append(paramSet.get("id"));
		}
		// 経度
		if(paramSet.containsKey("lat")) {
			sb.append(NanitabeConstants.LAT_QUERY_PREFIX);
			sb.append(paramSet.get("lat"));
		}
		// 緯度
		if(paramSet.containsKey("lng")) {
			sb.append(NanitabeConstants.LNG_QUERY_PREFIX);
			sb.append(paramSet.get("lng"));	
		}
		// キーワード
		if(paramSet.containsKey("keyword")) {
			sb.append(NanitabeConstants.KEY_WORD_QUERY_PREFIX);
			sb.append(paramSet.get("keyword"));	
		}
		// エリアコード
		if(paramSet.containsKey("areaCode")) {
			// コードの先頭文字を取得
			String initial = paramSet.get("areaCode").substring(0,1);
			switch(initial) {
				case NanitabeConstants.LEARGE_SERVICE_AREA_CODE_PREFIX_S:
					sb.append(NanitabeConstants.LEARGE_SERVICE_AREA_CODE_QUERY_PREFIX);
					sb.append(paramSet.get("areaCode"));
					break;
				case NanitabeConstants.LEARGE_AREA_CODE_PREFIX_Z:
					sb.append(NanitabeConstants.LEARGE_AREA_QUERY_PREFIX);
					sb.append(paramSet.get("areaCode"));
					break;
				case NanitabeConstants.MIDDLE_AREA_CODE_PREFIX_Y:
					sb.append(NanitabeConstants.MIDDLE_AREA_QUERY_PREFIX);
					sb.append(paramSet.get("areaCode"));
					break;
				case NanitabeConstants.SMALL_AREA_CODE_PREFIX_X:
					sb.append(NanitabeConstants.SMALL_AREA_QUERY_PREFIX);
					sb.append(paramSet.get("areaCode"));
					break;
				default:
					// TODO　ログ
					break;
			}
		}
		// ジャンルコード
		if(paramSet.containsKey("genre")) {
			sb.append(NanitabeConstants.GENRE_QUERY_PREFIX);
			sb.append(paramSet.get("genre"));	
		}
		// 予算コード
		if(paramSet.containsKey("budget")) {
			sb.append(NanitabeConstants.BUDGET_QUERY_PREFIX);
			sb.append(paramSet.get("budget"));	
		}
		
		return sb.toString();
	}
	
	/**
	 * ホットペッパーAPI通信処理メソッド
	 * @param reqUrl
	 * @return
	 * @throws Exception
	 */
	public String fetch(String reqUrl) throws Exception {
		
		// 結果格納変数
		HttpResponse<String> response;
		try {
			// ホットペッパーAPIに通信
			response = client.call(reqUrl);
		} catch (Exception e) {
			// 通信失敗 5000
			throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_5000);
		}
		// 通信に成功
		if (response.statusCode() == 200) {
			// レスポンスボディ取得
			try {
				String responseBody = this.analysisResponse(response.body());
				return responseBody;
			}catch(Exception e) {
				// ホットペッパーAPIエラーコード返却
				String errorMessage = e.getMessage();
				throw new Exception(errorMessage);
			}
		} else {
			// ステータス２００以外
			logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_1, response.statusCode());
			throw new Exception(String.valueOf(response.statusCode()));
		}
			
	}	

	/**
	 * レスポンスの件数を取得するメソッド
	 * @param responseJson
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public int getResultsReturned(String responseJson) throws JsonMappingException, JsonProcessingException {
		JsonNode node = mapper.readTree(responseJson);
		int resultsReturned = node.get("results").get("results_returned").asInt();
		return resultsReturned;
	}
	
	/**
	 * ホットペッパーAPIレスポンス解析メソッド
	 * @param responseBody
	 * @return　正常 レスポンスボディ　エラーコードあり、各エラーコードの例外
	 * @throws Exception
	 */
	public String analysisResponse(String responseBody) throws Exception {
		// レスポンス(Json)をパース	
		JsonNode node = null;
		try {
			node = mapper.readTree(responseBody);
		}catch(Exception e) {
			// json解析エラー
			logger.error(NanitabeLogConstants.JSON_NODE_PARSE_ERROR, e);
			throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_9998);
		}
		
		if (node.get("results").has("error")) {
			// エラーコードがある場合、ステータスにより処理をする
			String errorCode = node.get("results").get("error").get(0).get("code").asText();
			String errorMessage = node.get("results").get("error").get(0).get("message").asText();
			if (errorCode.equals("1000")) {
				// サーバ障害エラー
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_1000, errorCode, errorMessage);
				throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_1000);
			} else if (errorCode.equals("2000")) {
				// APIキーまたはIPアドレスの認証エラー
				// インシデントメール送信
				mailSendPublisher.ignite(
						mailProperties.getFrom(), 
						mailProperties.getAdmin(),
						mailProperties.getTitle2000(), 
						mailProperties.getMessage2000(),
						""
						);
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_2000, errorCode, errorMessage);
				throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_2000);
			} else if (errorCode.equals("3000")) {
				// パラメータ不正エラー
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_3000, errorCode, errorMessage);
				throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_3000);
			} else {
				// 未定義のエラー※ありえないはず
				
				// インシデントメール送信
				mailSendPublisher.ignite(
						mailProperties.getFrom(), 
						mailProperties.getAdmin(),
						mailProperties.getTitleNotice(), 
						mailProperties.getMessageNotice(),
						""
						);
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_9999, errorCode);
				throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_9999);
			}
		} else {
			// 検索件数を取得
			return responseBody;
		}
	}
	
	/**
	 * キーワード(keyWord)のチェックメソッド
	 * @param keyWord
	 * @return　true = 無効 false = 有効
	 */
	public boolean isInValidKeyWord(String keyWord) {
		String word[] = keyWord.split(",");
		for(int i = 0; i < word.length; i++) {
//			if(word[i].matches("[\\\/\-@+*;:#$%&!]")) {
//				return true;
//			}
		}
		// TODO キーワード　正規表現
		return false;
	}
	
	/**
	 * 予算コード(budget)のチェックメソッド
	 * @param budget
	 * @return　true = 無効 false = 有効
	 */
	public boolean isInValidBudgetCode(String budget) {
        String[] code = budget.split(",",2);
		for(int i = 0; i < code.length; i++) {
			if(!(Arrays.asList(NanitabeConstants.BUDGET_CODE_LIST).contains(code[0]))) {
				//　予算コードリストに含まれていない値が１つでもあった場合
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ジャンルコード(genre)のチェックメソッド
	 * @param genreCode
	 * @return true = 無効 false = 有効
	 */
	public boolean isInValidGenreCode(String genreCode) {
		String[] code = genreCode.split(",");
		for(int i = 0; i < code.length; i++) {
			if(!(Arrays.asList(NanitabeConstants.GENRE_CODE_LIST).contains(code[0]))) {
				//　genreコードに含まれていない値が１つでもあった場合
				return true;
			}
		}
		return false;
	}
	
	/**
	 * エリアコードのチェックメソッド
	 * @param areaCode
	 * @return　true = 無効 false = 有効
	 */
	public boolean isInValidAreaCode(String areaCode) {
		String[] code = areaCode.split(",", 5);
		if(code.length == 1 && !(code[0].startsWith(NanitabeConstants.SMALL_AREA_CODE_PREFIX_X))) {
			// エリアコードが１個 = (元・大・中）の規模で検索してきた場合
			if(
				!(code[0].startsWith(NanitabeConstants.LEARGE_AREA_CODE_PREFIX_Z)) &&
				!(code[0].startsWith(NanitabeConstants.LEARGE_SERVICE_AREA_CODE_PREFIX_S)) &&
				!(code[0].startsWith(NanitabeConstants.MIDDLE_AREA_CODE_PREFIX_Y))
			) {
				// (元・大・中）のエリアコードのプレフィックスに違反している場合
				return true;
			}
		} else {
			// 2個 ~ 5個までの場合
			for(int i = 0; i < code.length; i++) {
				if(!code[i].startsWith(NanitabeConstants.SMALL_AREA_CODE_PREFIX_X)) {
					// １つでも（小）エリアコードのプレフィックスに違反している場合
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ホットペッパーAPI レスポンス用オブジェクト作成
	 * 
	 * @param responseJson
	 * @return
	 * @throws Exception
	 */
	public ShopListResponse createResponse(String responseJson) throws Exception {

		// レスポンス用POJO
		ShopListResponse res = new ShopListResponse();
		List<Shop> shopList = new ArrayList<>();

		JsonNode node = null;
		try {
			// Json解析
			node = mapper.readTree(responseJson);
		} catch (JsonProcessingException e) {
			// json解析失敗
			logger.error(NanitabeLogConstants.HOTPEPPAR_LOGIC_ERROR_1);
			throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_9998);
		}

		// Jsonを読み込み
		JsonNode results = node.get("results");
		String resultsReturned = node.get("results").get("results_returned").asText();
		res.setResultsAvailable(resultsReturned); // 取得件数セット

		// 取得件数分、forを回し、shopの必要な情報をセットしていく
		int shopSize = Integer.parseInt(resultsReturned);
		for (int i = 0; i < shopSize; i++) {
			Shop shop = new Shop();
			shop.setId(results.get("shop").get(i).get("id").asText()); // お店ID
			shop.setName(results.get("shop").get(i).get("name").asText()); // 掲載店名
			shop.setPhotoPc(results.get("shop").get(i).get("photo").get("pc").get("l").asText()); // PC用画像URL
			shop.setPhotoMobile(results.get("shop").get(i).get("photo").get("mobile").get("l").asText()); // 携帯用画像URL
			shop.setAccess(results.get("shop").get(i).get("access").asText()); // 交通アクセス
			shop.setUrl(results.get("shop").get(i).get("urls").get("pc").asText()); // お店URL
			shop.setGenreName(results.get("shop").get(i).get("genre").get("name").asText()); // お店ジャンル名
			shop.setAddress(results.get("shop").get(i).get("address").asText()); // 住所
			shop.setCatchPhrase(results.get("shop").get(i).get("catch").asText()); // お店キャッチ
			shop.setBudgetAverage(results.get("shop").get(i).get("budget").get("average").asText()); // 平均予算
			shop.setOpen(results.get("shop").get(i).get("open").asText()); // 営業時間
			shop.setClose(results.get("shop").get(i).get("close").asText()); // 定休日
			shopList.add(shop);
		}
		res.setShop(shopList);
		res.setMessage("検索に成功しました。"); // 成功メッセージ
		res.setErrorCode("0");
		return res;
	}

}
