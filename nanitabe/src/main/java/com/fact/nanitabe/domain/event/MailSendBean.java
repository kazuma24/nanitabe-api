/**
 * 
 */
package com.fact.nanitabe.domain.event;

/**
 * メール情報作成用POJO
 * @author k-sato
 *
 */
public class MailSendBean {

	private String from;
	
	private String to;
	
	private String title;
	
	private String message;
	
	private String errorMessage;
	
	public MailSendBean(String from, String to, String title, String message, String errorMessage) {
		super();
		this.from = from;
		this.to = to;
		this.title = title;
		this.message = message;
		this.errorMessage = errorMessage;
	}
	
	public static MailSendBean of(String from, String to, String title, String message, String errorMessage) {
		return new MailSendBean(from, to, title, message, errorMessage);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
