package com.fact.nanitabe.application.resource.response;

/**
 * 現在地から検索APIレスポンス用
 * @author k-sato
 *
 */
public class Shop {

	// お店ID
	private String id;
	
	// 掲載店名
	private String name;
	
	// PC用画像URL
	private String photoPc;
	
	// 携帯用画像URL
	private String photoMobile;
	
	// 交通アクセス
	private String access;
	
	// お店URL
	private String url;
	
	// お店ジャンル名
	private String genreName;
	
	// 住所
	private String address;
	
	//　お店キャッチフレーズ
	private String catchPhrase;
	
	// 平均予算
	private String budgetAverage;
	
	// 営業時間
	private String open;
	
	// 定休日
	private String close;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoPc() {
		return photoPc;
	}

	public void setPhotoPc(String photoPc) {
		this.photoPc = photoPc;
	}

	public String getPhotoMobile() {
		return photoMobile;
	}

	public void setPhotoMobile(String photoMobile) {
		this.photoMobile = photoMobile;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCatchPhrase() {
		return catchPhrase;
	}

	public void setCatchPhrase(String catchPhrase) {
		this.catchPhrase = catchPhrase;
	}

	public String getBudgetAverage() {
		return budgetAverage;
	}

	public void setBudgetAverage(String budgetAverage) {
		this.budgetAverage = budgetAverage;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}
	
	
	
}
