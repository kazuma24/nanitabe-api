package com.fact.nanitabe.common.annotation;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fact.nanitabe.application.resource.response.JwtTokenCheckResponse;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.logic.JwtCommonLogic;
import com.fact.nanitabe.common.property.MailProperties;
import com.fact.nanitabe.domain.event.MailSendEventPublisher;

/** 
 * JWTトークンが必要なAPIに付与するアノテーション
 * チェック項目
 * ・JWTトークンの存在確認
 * 　 あり→検証へ
 *   なし→401
 * ・JWTトークンの検証
 * 　 有効→メソッド実行
 *   無効→403
 * @author k-sato
 */
@Aspect
@Component
public class JwtTokenCheckFilter {

	@Autowired
	JwtCommonLogic jwtLogic;
	
	@Autowired
	MailProperties mailProperties;
	
	@Autowired
	MailSendEventPublisher mailSendPublisher;

	private static final Logger logger = LogManager.getLogger(JwtTokenCheckFilter.class);

	@Around("@annotation(com.fact.nanitabe.common.annotation.JwtTokenCheck)")
	public Object jwtTokenCheck(ProceedingJoinPoint joinPoint) throws Throwable {

		// レスポンス
		JwtTokenCheckResponse response = new JwtTokenCheckResponse();
		
		// リクエスト情報を取得
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
		HttpServletRequest request = servletRequestAttributes.getRequest();

		// リクエストのヘッダーAuthorizationの値を取得
		String jwtToken = request.getHeader("Authorization");
		
		if (jwtToken == null || !jwtToken.startsWith("Bearer")) {
			// JWTトークンがない、またはスキーマ無効
			logger.error("JWTトークンがありません。 実行メソッド" + joinPoint.getSignature());
			response.setMessage("ログインまたは会員登録が必要です。");
			return new ResponseEntity<JwtTokenCheckResponse>(response,HttpStatus.UNAUTHORIZED); // 401
		}

		// トークンが不正か
		boolean isForbiden = false;
		
		try {
			// JWTトークンの検証を実施
			jwtLogic.verifyJWT(jwtToken);

		} catch (AlgorithmMismatchException e) {
			// ヘッダーに記載されているアルゴリズム(alg)が等しくない
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_5, jwtToken);
			isForbiden = true;

		} catch (SignatureVerificationException e) {
			// 署名が無効な場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_4, jwtToken);
			isForbiden = true;

		} catch (TokenExpiredException e) {
			// トークンの有効期限が切れている場合 TODO
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_3, jwtToken);
			isForbiden = true;
			
		} catch (InvalidClaimException e) {
			// クレーム(ペイロード)に予想とは異なる値が含まれている場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_2, jwtToken);
			isForbiden = true;

		} catch (JWTVerificationException e) {
			// 上記以外で検証失敗の場合
			logger.error(NanitabeLogConstants.JWTPROVIDER_ERROR_1, jwtToken);
			isForbiden = true;

		}
		
		if(isForbiden) {
			// トークン無効 403
			response.setMessage("アクセスが拒否されました。");
			return new ResponseEntity<JwtTokenCheckResponse>(response, HttpStatus.FORBIDDEN); // 403
		}
		
		// JWTトークン有効
		logger.info(NanitabeLogConstants.JWTPROVIDER_INFO_3);
		
		// メソッド実行
		Object res = joinPoint.proceed();
		return res;
	}
}

