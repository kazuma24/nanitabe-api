package com.fact.nanitabe.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource("classpath:hotpeppar.properties")
@ConfigurationProperties(prefix = "hotpeppar")
@Component
public class HotPepparProperties {

	// グルメサーチAPIURL
	private String gourmandSearchApi;
	
	// 小エリアマスタAPI
	private String smallAreaApiUrl;
	
	// APIキー
	private String apiKey;
	

	public String getGourmandSearchApi() {
		return gourmandSearchApi;
	}

	public void setGourmandSearchApi(String gourmandSearchApi) {
		this.gourmandSearchApi = gourmandSearchApi;
	}

	public String getSmallAreaApiUrl() {
		return smallAreaApiUrl;
	}

	public void setSmallAreaApiUrl(String smallAreaApiUrl) {
		this.smallAreaApiUrl = smallAreaApiUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	
}
