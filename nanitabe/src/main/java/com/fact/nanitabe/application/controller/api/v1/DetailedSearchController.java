/**
 * 
 */
package com.fact.nanitabe.application.controller.api.v1;

import org.apache.commons.lang3.StringUtils;
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
 * nanitabe 詳細検索API .ver1
 * @author k-sato
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class DetailedSearchController {

	@Autowired
	NanitabeServiceImpl service;
	
	private static final Logger logger = LogManager.getLogger(DetailedSearchController.class);
	
	@ExceptionNotice
	@RequestMapping(value = "/detailedsearch", method = {RequestMethod.GET })
	public ResponseEntity<ShopListResponse> detailedSearch(
			@RequestParam(name = "keyWord", required = false) String keyWord,
			@RequestParam(name = "areaCode", required = false) String areaCode,
			@RequestParam(name = "genre", required = false) String genre,
			@RequestParam(name = "budget", required = false) String budget,
			@RequestParam(name = "lat", required = false) String lat,
			@RequestParam(name = "lmg", required = false) String lng){
		
		// レスポンス格納用
		ShopListResponse response = new ShopListResponse();
		
		if(StringUtils.isEmpty(keyWord) && StringUtils.isEmpty(areaCode) && 
			StringUtils.isEmpty(genre) && StringUtils.isEmpty(budget) && 
			StringUtils.isEmpty(lat) && StringUtils.isEmpty(lng)) {
			// 検索条件がない場合
			response.setMessage(NanitabeMessageConstants.DETAILED_SEARCH_MESSAGE_400_1);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		// キーワード,エリア,ジャンル,予算コードチェック
		if(service.isInValidAll(keyWord, areaCode, genre, budget)) {
			response.setMessage(NanitabeMessageConstants.DETAILED_SEARCH_MESSAGE_400);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		// エリアコードと経度緯度の整合性をチェックし処理を分岐
		boolean isAreaCodeSearch;
		if(StringUtils.isEmpty(areaCode) && (StringUtils.isNotEmpty(lng) && StringUtils.isNotEmpty(lat))) {
			// 位置情報で検索
			isAreaCodeSearch = false;
		}else if(StringUtils.isNotEmpty(areaCode) && (StringUtils.isEmpty(lng) && StringUtils.isEmpty(lat))) {
			// エリアコードから検索
			isAreaCodeSearch = true;
		}else {
			// 整合性なし
			response.setMessage(NanitabeMessageConstants.DETAILED_SEARCH_MESSAGE_400_1);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			// 詳細検索API処理開始
			logger.info(NanitabeLogConstants.PROCESS_START,"詳細検索API");
			if(isAreaCodeSearch) {
				// エリアコード指定で詳細検索
				response = service.tryDetailedSearch(keyWord, genre, budget, areaCode);
			} else {
				// 位置情報から詳細検索
				response = service.tryDetailedSearch(keyWord, genre, budget, lat, lng);
			}
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			// サーバーエラー(ホットペッパーAPIエラー）の場合
			response.setMessage(NanitabeMessageConstants.SEARCH_FAILURE_MESSAGE);
			response.setErrorCode(e.getMessage());
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
}
