package com.fact.nanitabe.common.logic;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.property.JwtProperties;

/**
 * JWTにかかわる機能を提供するプロバイダークラス
 * @author k-sato
 */
@Component
public class JwtCommonLogic {

	@Autowired
	JwtProperties jwtProperties;

	private static final Logger logger = LogManager.getLogger(JwtCommonLogic.class);

	/**
	 * JWTトークン生成メソッド
	 * 
	 * @param userId ユーザ一意のID
	 * @return JWTトークン
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String generateJwtToken(int userId) {
		// ログ
		logger.info(NanitabeLogConstants.JWTPROVIDER_INFO_1);
		// 鍵を用いてHS256で署名
		Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecretKey());
		// ヘッダー部設定
		Map<String, Object> jwtHeader = new LinkedHashMap<>();
		jwtHeader.put("typ", "JWT"); // トークン種類
		// 1週間後取得
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 7);

		// ペイロード部設定
		Map<String, Object> jwtPayload = new LinkedHashMap<>();
		jwtPayload.put("iss", "https://nanitabe-api.herokuapp.com/"); // JWTを発行した者(サーバー)の識別子
		jwtPayload.put("iat", new Date()); // 発行日時
		jwtPayload.put("exp", cal.getTime()); // 有効期限の終了日時 (1週間)
		jwtPayload.put("sub", userId); // ユーザーID

		// JWTトークン生成
		String token = JWT.create().withHeader(jwtHeader).withPayload(jwtPayload).sign(algorithm);

		// スキーマ追加
		String jwtToken = jwtProperties.getSchema() + " " + token;
		// ログ
		logger.info(NanitabeLogConstants.JWTPROVIDER_INFO_2);
		// JWTトークン返却
		return jwtToken;
	}

	/**
	 * JWTトークン検証メソッド
	 * 
	 * @param jwtToken JWTトークン
	 * @return 検証済みJWTトークン(Bearer抜き)
	 */
	public String verifyJWT(String jwtToken) throws AlgorithmMismatchException, SignatureVerificationException,
			TokenExpiredException, InvalidClaimException, JWTVerificationException {

		// シックレットキーとアルゴリズム設定
		Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecretKey());

		// トークン署名の検証に使用されるアルゴリズムを備えた、JWT確認用のビルダーを生成
		JWTVerifier verifier = JWT.require(algorithm).build();

		// トークン部分抽出
		String token = jwtToken.substring(6).trim();

		try {

			// JWTトークンの検証を実施
			verifier.verify(token);

		} catch (AlgorithmMismatchException e) {
			// ヘッダーに記載されているアルゴリズム(alg)が等しくない
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_5, jwtToken);
			throw e;
		} catch (SignatureVerificationException e) {
			// 署名が無効な場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_4, jwtToken);
			throw e;
		} catch (TokenExpiredException e) {
			// トークンの有効期限が切れている場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_3, jwtToken);
			throw e;
		} catch (InvalidClaimException e) {
			// クレーム(ペイロード)に予想とは異なる値が含まれている場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_2, jwtToken);
			throw e;
		} catch (JWTVerificationException e) {
			// 上記以外で検証失敗の場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_1, jwtToken);
			throw e;
		}

		// 検証成功の場合スキーマ抜きのTokenを返却
		return token;

	}

	/**
	 * JWTトークンのヘッダーとペイロードを取得するメソッド
	 * 
	 * @param jwtToken JWTトークン(検証済み)
	 * @return ヘッダー、ペイロード
	 */
	public Map<String, Object> getJwtTokenValue(String jwtToken) {

		// jwtTokenをでコード
		DecodedJWT jwt = JWT.decode(jwtToken);

		// jwtTokenの各値をMapにセット
		Map<String, Object> jwtMap = new LinkedHashMap<>();
		jwtMap.put("typ", jwt.getType());
		jwtMap.put("alg", jwt.getAlgorithm());
		jwtMap.put("iss", jwt.getClaim("iss").asString());
		jwtMap.put("iat", jwt.getClaim("iat").asDate());
		jwtMap.put("exp", jwt.getClaim("exp").asDate());
		jwtMap.put("sub", jwt.getClaim("sub").asString());

		return jwtMap;
	}
	
	/**
	 * JWTトークンのuserID取得
	 * @param jwtToken
	 * @return　userID　ユーザーID
	 * @throws Exception 
	 */
	public Integer getUserId(String jwtToken) throws Exception {
		// jwtTokenをでコード
		try {
			DecodedJWT jwt = JWT.decode(jwtToken);
			String strUserId = jwt.getSubject();
		    return Integer.valueOf(strUserId);
		}catch(Exception e) {
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_6, jwtToken);
			throw e;
		}
	}
	
	/**
	 * スキーマBearerを抜く
	 * @param jwtTokne
	 * @return
	 */
	public String withoutSchema(String jwtToken) {
		String token = jwtToken.substring(6).trim();
		return token;
	}
}
