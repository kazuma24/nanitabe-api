package com.fact.nanitabe.application.resource.response;

import java.util.List;

/**
 * お店情報リストレスポンス用
 * @author k-sato
 *
 */
public class ShopListResponse {

	// 条件にマッチした、検索結果の全件数
	private String resultsAvailable;
	
	// お店の情報リスト
	private List<Shop> shop;
	
	// メッセージ
	private String message;
	
	// エラーコード
	private String errorCode;
	

	public String getResultsAvailable() {
		return resultsAvailable;
	}

	public void setResultsAvailable(String resultsAvailable) {
		this.resultsAvailable = resultsAvailable;
	}

	public List<Shop> getShop() {
		return shop;
	}

	public void setShop(List<Shop> shop) {
		this.shop = shop;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
