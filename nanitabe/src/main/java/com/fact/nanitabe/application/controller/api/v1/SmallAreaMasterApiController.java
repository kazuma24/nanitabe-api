/**
 * 
 */
package com.fact.nanitabe.application.controller.api.v1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fact.nanitabe.common.annotation.ExceptionNotice;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.domain.service.NanitabeServiceImpl;

/**
 * @author k-sato
 * 小エリアマスタAPIの結果を返すAPI
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class SmallAreaMasterApiController {
	
	@Autowired
	NanitabeServiceImpl service;
	
	private static final Logger logger = LogManager.getLogger(SmallAreaMasterApiController.class);
	
	@ExceptionNotice
	@RequestMapping(value = "/getsmallarea", method = { RequestMethod.GET })
	public ResponseEntity<String> trySmallArea(
			@RequestParam(name = "middleArea", required = true) String middleArea
			) {
		
		try {
			logger.info(NanitabeLogConstants.PROCESS_START,"小エリアマスタAPI");
			// 小エリアマスタAPI処理実施
			String json = service.trySmallAreaApi(middleArea);
			if(json.equals("badAreaCode")) {
				return new ResponseEntity<String>("",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<String>(json,HttpStatus.CREATED);
		}catch(Exception e) {
			// 処理失敗
			return new ResponseEntity<String>("",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
