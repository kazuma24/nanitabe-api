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

import com.fact.nanitabe.application.resource.response.ShopListResponse;
import com.fact.nanitabe.common.annotation.ExceptionNotice;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.domain.service.NanitabeServiceImpl;

/**
 * nanitabea 現在地から検索API .ver1
 * 
 * @author k-sato
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class GetCurrentLocationShopController {

	@Autowired
	NanitabeServiceImpl service;

	private static final Logger logger = LogManager.getLogger(GetCurrentLocationShopController.class);

	@ExceptionNotice
	@RequestMapping(value = "/getcurrentlocationshop", method = {RequestMethod.GET })
	public ResponseEntity<ShopListResponse> getCurrentLocationShop(
			@RequestParam("lat") String lat,
			@RequestParam("lng") String lng) {
		
		try {
			// 現在地から検索APIのサービスを呼ぶ
			logger.info(NanitabeLogConstants.PROCESS_START,"現在地から検索API");
			ShopListResponse response = service.tryGetShop(lat, lng);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			// サーバーエラー(ホットペッパーAPIエラー）の場合
			ShopListResponse response = new ShopListResponse();
			response.setMessage(NanitabeMessageConstants.SEARCH_FAILURE_MESSAGE);
			response.setErrorCode(e.getMessage());
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
