/**
 * 
 */
package com.fact.nanitabe.infrastructure.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

/**
 * @author k-sato
 *
 */
@Component
@Entity
@Table(name = "favos")
public class Favos {

	@ManyToOne()
    @JoinColumn(name = "userId", 
				referencedColumnName = "userId", 
				insertable = false, 
				updatable = false)
    private Users user;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer userId;
	
	private String storeId;
	
	private String name;
	
	private String photoPc;
	
	private String photoMobile;
	
	private String access;
	
	private String url;
	
	private String genreName;
	
	private String address;
	
	private String catchPhrase;
	
	private String budgetAverage;
	
	private String open;
	
	private String close;

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
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
