package com.fact.nanitabe.application.resource.response;

import java.util.List;

public class LoginResponse {

    private String JwtToken;
	
	private List<String> message;
	
	public String getJwtToken() {
		return JwtToken;
	}
	public void setJwtToken(String jwtToken) {
		JwtToken = jwtToken;
	}
	public List<String> getMessage() {
		return message;
	}
	public void setMessage(List<String> message) {
		this.message = message;
	}
}
