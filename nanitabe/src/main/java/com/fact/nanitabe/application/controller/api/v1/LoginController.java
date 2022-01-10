package com.fact.nanitabe.application.controller.api.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fact.nanitabe.application.resource.request.LoginRequest;
import com.fact.nanitabe.application.resource.response.LoginResponse;
import com.fact.nanitabe.common.annotation.ExceptionNotice;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.common.exception.AuthenticationFailureException;
import com.fact.nanitabe.domain.service.NanitabeServiceImpl;

/**
 * nanitabe ログインAPI .ver1
 * @author k-sato
 *
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class LoginController {

	@Autowired
	NanitabeServiceImpl service;
	
	private static final Logger logger = LogManager.getLogger(LoginController.class);

	/**
	 * ログインAPI コントローラー
	 * @param request リクエストボディ email password
	 * @param jwtToken リクエストヘッダー Jwtトークン
	 * @param result バリデーション結果
	 * @return
	 */
	@ExceptionNotice
	@RequestMapping(value = "/login", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResponse> login(
			@RequestHeader(name = "Authorization", required = false) Optional<String> jwtToken, 
			@RequestBody @Validated LoginRequest request,
			BindingResult result) {

		// レスポンス格納用
		LoginResponse response = new LoginResponse();

		// リクエストパラメータバリデーション
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			response.setMessage(errorList);
			return new ResponseEntity<LoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		// パラメータ取得
		String email = request.getEmail();
		String password = request.getPassword();
		
		logger.info(NanitabeLogConstants.PROCESS_START,"ログインAPI");
		
		// ヘッダーのJWTトークン存在チェック
		if (jwtToken.isPresent()) {
			// ある場合
			try {
				
				// JWT検証→ログイン実施
				String newJwtToken = service.tryLoginJwtExists(jwtToken.get(), email, password);
				
				//　成功
				response.setJwtToken(newJwtToken);
				response.setMessage(Arrays.asList("現在はログイン中です。"));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
			} catch (JWTVerificationException e) {
				// トークン無効 403
				response.setMessage(Arrays.asList(NanitabeMessageConstants.LOGIN_ACCESS_DENIED));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.FORBIDDEN);
			} catch (AuthenticationFailureException e) {
				// 認証失敗 401
				response.setMessage(Arrays.asList(NanitabeMessageConstants.LOGIN_AUTH_FAILURE));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.UNAUTHORIZED);
			} catch (Exception e) {
				// サーバーエラー 500
				response.setMessage(Arrays.asList(NanitabeMessageConstants.SYSTEM_ERROR));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {
			// ない場合
			try {
				// ログイン実施
				String newJwtToken = service.tryLogin(email, password);
				
				// ログイン成功 200
				response.setMessage(Arrays.asList("ログインに成功しました。"));
				response.setJwtToken(newJwtToken);
				return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
			} catch (AuthenticationFailureException e) {
				// 認証失敗 401
				response.setMessage(Arrays.asList(NanitabeMessageConstants.LOGIN_AUTH_FAILURE));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.UNAUTHORIZED);
			} catch (Exception e) {
				// サーバーエラー 500
				response.setMessage(Arrays.asList(NanitabeMessageConstants.SYSTEM_ERROR));
				return new ResponseEntity<LoginResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
