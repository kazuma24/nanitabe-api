package com.fact.nanitabe.domain.service;

import javax.transaction.Transactional;

import org.json.JSONException;

import com.fact.nanitabe.application.resource.response.ShopListResponse;

public interface NanitabeService {

    // 新規登録API
	
	// Emailの重複を確認する
	public boolean isDuplicateEmail(String email) throws Exception;
	
	// 新規登録処理を実施する
	@Transactional
	public String newRegistration(String email, String password) throws Exception;
	
	// 現在地から検索API
	public ShopListResponse tryGetShop(String lat, String lng) throws Exception;
	
	// ログイン(JWT)あり
	public String tryLoginJwtExists(String jwt, String email, String password) throws Exception;
	
	//　ログイン(JWT)なし
	public String tryLogin(String email, String password) throws Exception;
	
	// エリア、ジャンル、予算まとめてチェック
	public boolean isInValidAll(String keyWord, String areaCode, String genre, String budget);
		
	// 詳細検索API処理
	public ShopListResponse tryDetailedSearch(String...params) throws Exception;
	
	// お気に入り登録API処理
	public String tryFavoriteRegiste(String jwt, String id) throws Exception;
	
	//　お気に入り削除API処理
	public void tryFavoriteDelete(String jwt, String id) throws Exception;
	
	// お気に入り一覧取得API処理
	public ShopListResponse tryFavoriteGet(String jwtToken) throws Exception;
	
	//　小エリアマスタAPI処理
	public String trySmallAreaApi(String middleArea) throws JSONException, Exception;
}
