package com.fact.nanitabe.domain.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fact.nanitabe.common.constants.NanitabeLogConstants;

/**
 * ホットペッパーAPIと通信するクラス
 * 
 * @author k-sato
 *
 */
@Component
public class HotPepparClient {
	
	private static final Logger logger = LogManager.getLogger(HotPepparClient.class);

	/**
	 * ホットペッパーAPI実行メソッド
	 * 
	 * @param url
	 * @return
	 * @throws HttpConnectTimeoutException
	 * @throws Exception
	 */
	public HttpResponse<String> call(String url) throws HttpConnectTimeoutException, Exception {

		try {
			// HTTPリクエスト情報生成
			HttpRequest requestUrl = HttpRequest.newBuilder(new URI(url)).GET().build();

			// HTTPクライアント作成
			HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
					.connectTimeout(Duration.ofSeconds(10)).build();

			logger.info(NanitabeLogConstants.HOTPEPPAR_API_INFO, requestUrl);
			
			// APIに通信
			HttpResponse<String> response = httpClient.send(requestUrl, HttpResponse.BodyHandlers.ofString());

			return response;
			
		} catch (HttpConnectTimeoutException e) {
			// タイムアウトの場合
			logger.error(NanitabeLogConstants.HOTPEPPAR_API_TIMEOUT_ERROR);
			throw e;
		} catch (Exception e) {
			// 通信失敗
			logger.error(NanitabeLogConstants.HOTPEPPAR_API_SERVER_ERROR, e.getMessage());
			throw e;
		}

	}

}
