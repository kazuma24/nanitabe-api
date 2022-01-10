/**
 * 
 */
package com.fact.nanitabe.application.controller.api.v1;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fact.nanitabe.application.resource.request.FavoriteRequest;
import com.fact.nanitabe.application.resource.response.FavoriteResponse;
import com.fact.nanitabe.application.resource.response.ShopListResponse;
import com.fact.nanitabe.common.annotation.ExceptionNotice;
import com.fact.nanitabe.common.annotation.JwtTokenCheck;
import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.common.exception.DuplicateException;
import com.fact.nanitabe.domain.service.NanitabeServiceImpl;

/**
 * nanitabe お気に入り関連API .ver1
 * @author k-sato
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://quizzical-spence-ad9574.netlify.app"})
public class FavoriteController {

	@Autowired
	NanitabeServiceImpl service;
	
	private static final Logger logger = LogManager.getLogger(FavoriteController.class);
	
	/*
	 * お気に入り登録
	 */
	@JwtTokenCheck
	@ExceptionNotice
	@RequestMapping(value = "/favorite", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FavoriteResponse> favoriteRegiste(
			@RequestHeader(name = "Authorization", required = false) String jwtToken,
			@RequestBody FavoriteRequest request) {
		
		FavoriteResponse response = new FavoriteResponse();
		
		if(StringUtils.isBlank(request.getShopId())) {
			// パラメーター（おみせID）がない場合
			response.setMessage(NanitabeMessageConstants.FAVORITE_REGISTE_BAD_REQUEST);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if(!request.getShopId().matches("^J[0-9]{9}")) {
			// おみせIDチェック(Jで始まりかつ9桁の半角数字
			response.setMessage(NanitabeMessageConstants.FAVORITE_BAD_REQUEST_ID);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			// お気に入り登録実施
			logger.info(NanitabeLogConstants.PROCESS_START,"お気に入り登録API");
			String storeName = service.tryFavoriteRegiste(jwtToken, request.getShopId());
			response.setMessage(String.format(NanitabeMessageConstants.FAVORITE_SUCCESS, storeName));
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.CREATED);
		}catch(DuplicateException e) {
			// お気に入り済み
			logger.info(NanitabeLogConstants.FAVORITE_DUPLICATE_INFO);
			response.setMessage(NanitabeMessageConstants.FAVORITE_DUPLICATE);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			// サーバーエラー
			logger.error(NanitabeLogConstants.FAVORITE_SYSTEM_ERROR, e);
			response.setMessage(NanitabeMessageConstants.SYSTEM_ERROR);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*
	 * お気に入り削除
	 */
	@JwtTokenCheck
	@ExceptionNotice
	@RequestMapping(value = "/favorite/{shopId}", method = { RequestMethod.DELETE })
	public ResponseEntity<FavoriteResponse> favoriteDelete(
			@RequestHeader(name = "Authorization", required = false) String jwtToken,
			@PathVariable(name = "shopId", required = false) String shopId) {
		
		FavoriteResponse response = new FavoriteResponse();
		
		if(StringUtils.isBlank(shopId)) {
			// パラメーター（おみせID）がない場合
			response.setMessage(NanitabeMessageConstants.FAVORITE_DELETE_BAD_REQUEST);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(!shopId.matches("^J[0-9]{9}")) {
			// おみせIDチェック(Jで始まりかつ9桁の半角数字
			response.setMessage(NanitabeMessageConstants.FAVORITE_DELETE_BAD_REQUEST_ID);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			// お気に入り削除実施
			service.tryFavoriteDelete(jwtToken, shopId);
			response.setMessage(NanitabeMessageConstants.FAVORITE_DELETE_SUCCESS);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.OK);
		}catch(Exception e) {
			// サーバーエラー
			logger.error(NanitabeLogConstants.FAVORITE_SYSTEM_ERROR, e);
			response.setMessage(NanitabeMessageConstants.SYSTEM_ERROR);
			return new ResponseEntity<FavoriteResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * お気に入り一覧取得
	 * @param jwtToken
	 * @return
	 */
	@JwtTokenCheck
	@ExceptionNotice
	@RequestMapping(value = "/favorite", method = { RequestMethod.GET })
	public ResponseEntity<ShopListResponse> favoriteGet(
			@RequestHeader(name = "Authorization", required = false) String jwtToken) {
		
		ShopListResponse response = new ShopListResponse();
		
		try {
			// お気に入り削除実施
			response = service.tryFavoriteGet(jwtToken);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.OK);
		}catch(Exception e) {
			// サーバーエラー
			logger.error(NanitabeLogConstants.FAVORITE_GET_SYSTEM_ERROR, e);
			response.setMessage(NanitabeMessageConstants.SYSTEM_ERROR);
			return new ResponseEntity<ShopListResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
