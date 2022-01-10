package com.fact.nanitabe.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource("classpath:mail.properties")
@ConfigurationProperties(prefix = "mail")
@Component
public class MailProperties {

	// メール受信者
	private String admin;
	// メール送信者
	private String from;
	// アカウントパスワード
	private String accountPass;
	
	//　メールタイトル
	private String title2000;
	private String titleNotice;
	
	// メッセージ
	private String message2000;
	private String messageNotice;
	
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getAccountPass() {
		return accountPass;
	}
	public void setAccountPass(String accountPass) {
		this.accountPass = accountPass;
	}
	public String getTitle2000() {
		return title2000;
	}
	public void setTitle2000(String title2000) {
		this.title2000 = title2000;
	}
	public String getMessage2000() {
		return message2000;
	}
	public void setMessage2000(String message2000) {
		this.message2000 = message2000;
	}
	public String getTitleNotice() {
		return titleNotice;
	}
	public void setTitleNotice(String titleNotice) {
		this.titleNotice = titleNotice;
	}
	public String getMessageNotice() {
		return messageNotice;
	}
	public void setMessageNotice(String messageNotice) {
		this.messageNotice = messageNotice;
	}
	
	
}
