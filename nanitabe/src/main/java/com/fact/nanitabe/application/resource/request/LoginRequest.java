package com.fact.nanitabe.application.resource.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fact.nanitabe.common.constants.NanitabeMessageConstants;

/**
 * ログインリクエスト用クラス
 * @author k-sato
 *
 */
public class LoginRequest {

	@NotBlank(message=NanitabeMessageConstants.NEWREGISTER_MESSAGE_1)
	@Email(message=NanitabeMessageConstants.NEWREGISTER_MESSAGE_2)
	private String email;
	
	@NotBlank(message=NanitabeMessageConstants.NEWREGISTER_MESSAGE_4)
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
