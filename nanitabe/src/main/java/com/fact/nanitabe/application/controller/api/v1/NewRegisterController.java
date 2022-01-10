package com.fact.nanitabe.application.controller.api.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fact.nanitabe.application.resource.request.NewRegisterRequest;
import com.fact.nanitabe.application.resource.response.NewRegisterResponse;
import com.fact.nanitabe.common.annotation.ExceptionNotice;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.domain.service.NanitabeServiceImpl;

/**
 * nanitabea　新規登録API　.ver1
 * @author k-sato
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class NewRegisterController {

	@Autowired
	NanitabeServiceImpl service;

	private static final Logger logger = LogManager.getLogger(NewRegisterController.class);

	/**
	 * 新規登録API
	 * 
	 * @param request リクエストパラメーター
	 * @return response レスポンス（JWT)
	 * @throws Exception 
	 */
	@ExceptionNotice
	@RequestMapping(value = "/newregister", method = {RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NewRegisterResponse> newRegister(
			@RequestBody @Validated NewRegisterRequest request, 
			BindingResult result) throws Exception {

		// レスポンス
		NewRegisterResponse response = new NewRegisterResponse();
		
		// リクエストパラメータバリデーション
		if (result.hasErrors()) {
            List<String> errorList = new ArrayList<String>();
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
            response.setMessage(errorList);
            return new ResponseEntity<NewRegisterResponse>(response, HttpStatus.BAD_REQUEST);
        }
		
		logger.error(NanitabeLogConstants.NEWREGISTER_INFO_4);
		
		try {
			//　パラメータ取得
			String email = request.getEmail();
			String password = request.getPassword();
			
			// メール重複チェック
			if(service.isDuplicateEmail(email)) {
				logger.error(NanitabeLogConstants.NEWREGISTER_ERROR_5, email);
				response.setMessage(Arrays.asList(NanitabeMessageConstants.NEWREGISTER_MESSAGE_3));
				return new ResponseEntity<NewRegisterResponse>(response, HttpStatus.BAD_REQUEST);
			}
			logger.info(NanitabeLogConstants.NEWREGISTER_INFO_1);
			
			logger.info(NanitabeLogConstants.PROCESS_START,"新規登録API");
			// 新規登録処理実施
			String jwt = service.newRegistration(email, password);
			
			// JWTとレスポンスを返却
			response.setJwtToken(jwt);
			response.setMessage(Arrays.asList(NanitabeMessageConstants.NEWREGISTER_MESSAGE_7));
			logger.info(NanitabeLogConstants.NEWREGISTER_INFO_3);
			return new ResponseEntity<NewRegisterResponse>(response, HttpStatus.CREATED);
			
		} catch (Exception e) {
			// サーバーエラー
			response.setMessage(Arrays.asList(NanitabeMessageConstants.NEWREGISTER_MESSAGE_8));
			logger.error(NanitabeLogConstants.NEWREGISTER_ERROR_4, e.toString());
			return new ResponseEntity<NewRegisterResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
