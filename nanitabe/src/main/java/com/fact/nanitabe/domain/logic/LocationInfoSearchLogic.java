/**
 * 
 */
package com.fact.nanitabe.domain.logic;

import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fact.nanitabe.common.constants.NanitabeLogConstants;
import com.fact.nanitabe.common.constants.NanitabeMessageConstants;
import com.fact.nanitabe.common.logic.HotPepparCommonLogic;
import com.fact.nanitabe.common.property.MailProperties;
import com.fact.nanitabe.domain.client.HotPepparClient;
import com.fact.nanitabe.domain.event.MailSendEventPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author k-sato
 * 位置情報から検索するAPIのロジッククラス
 */
@Component
public class LocationInfoSearchLogic implements HotPepparLogic {
	
	@Autowired
	MailSendEventPublisher mailSendPublisher;

	@Autowired
	HotPepparClient client;
	
	@Autowired
	MailProperties mailProperties;
	
	@Autowired
	HotPepparCommonLogic hpCommonLogic;
	
	@Autowired
	ObjectMapper mapper;

	private static final Logger logger = LogManager.getLogger(LocationInfoSearchLogic.class);
	
	/**
	 * 現在地から検索API、ロジックメソッド 取得件数が10件以上取得できるまで、ホットペッパーAPIの検索範囲を広げて叩く。
	 * 検索範囲最大(range=5)で最後のリクエストとし、10件未満でもレスポンスを返す。
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@Override
	public String fetchShop(String url) throws Exception {

		// 検索範囲
		int range = 1;
		// 検索範囲クエリパラメーター
		final String QUERY_PREFIX = "&range=";
		// リトライするか判定
		boolean isRetry = true;
		// リトライ回数
		int retryCount = 0;
		// レスポンスボディ格納用変数
		String responseBody = "";

		while (isRetry) {

			if (retryCount > 0) {
				// リトライが１回以上の場合、リトライ数+1で検索範囲を設定
				range = retryCount + 1;
			}

			if (range == 5) {
				// 検索範囲最大(range=5)で最後のリクエストとする
				isRetry = false;
			}

			// 結果格納変数
			HttpResponse<String> response;
			// リクエストURL作成
			String reqUrl = url + QUERY_PREFIX + range;
			try {
				// ホットペッパーAPIに通信
				response = client.call(reqUrl);
			} catch (HttpConnectTimeoutException e) {
				// タイムアウトの場合
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_TIMEOUT_ERROR_1, retryCount);
				if (!isRetry) {
					// タイムアウトが連続した場合
					logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_4000);
					throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_4000);
				} else {
					retryCount++; // リトライ回数をカウント
					continue;
				}
			} catch (Exception e) {
				// 通信失敗
				throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_5000);
			}

			// 通信に成功
			if (response.statusCode() == 200) {
				// レスポンスボディ取得
				responseBody = response.body();
				// レスポンス(Json)をパース
				JsonNode node = mapper.readTree(responseBody);

				if (node.get("results").has("error")) {
					// エラーコードがある場合、ステータスにより処理をする
					String errorCode = node.get("results").get("error").get(0).get("code").asText();
					String errorMessage = node.get("results").get("error").get(0).get("message").asText();
					if (errorCode.equals("1000")) {
						// サーバ障害エラー
						logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_1000, errorCode, errorMessage);
						throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_1000);
					} else if (errorCode.equals("2000")) {
						// APIキーまたはIPアドレスの認証エラー
						// インシデントメール送信
						mailSendPublisher.ignite(
								mailProperties.getFrom(), 
								mailProperties.getAdmin(),
								mailProperties.getTitle2000(), 
								mailProperties.getMessage2000(),
								""
								);
						logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_2000, errorCode, errorMessage);
						throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_2000);
					} else if (errorCode.equals("3000")) {
						// パラメータ不正エラー
						logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_3000, errorCode, errorMessage);
						throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_3000);
					} else {
						// 未定義のエラー※ありえないはず
						
						// インシデントメール送信
						mailSendPublisher.ignite(
								mailProperties.getFrom(), 
								mailProperties.getAdmin(),
								mailProperties.getTitleNotice(), 
								mailProperties.getMessageNotice(),
								""
								);
						logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_9999, errorCode);
						throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_9999);
					}
				} else {
					// 検索件数を取得
					int resultsReturned = hpCommonLogic.getResultsReturned(responseBody);
					if (resultsReturned < 10) {
						// 10件未満
						if (!isRetry) {
							// リトライしない(最後のリクエスト）
							logger.info(NanitabeLogConstants.HOTPEPPAR_API_INFO_2, resultsReturned);
							return responseBody;
						}
						logger.info(NanitabeLogConstants.HOTPEPPAR_API_INFO_1, retryCount, resultsReturned);
						retryCount++; // リトライ回数をカウント
						continue;
					} else {
						logger.info(NanitabeLogConstants.HOTPEPPAR_API_INFO_3, resultsReturned);
						return responseBody;
					}
				}
			} else {
				// ステータス２００以外
				logger.error(NanitabeLogConstants.HOTPEPPAR_API_ERROR_1, response.statusCode());
				throw new Exception(String.valueOf(response.statusCode()));
			}
		}
		// 未定義エラーの場合
		throw new Exception(NanitabeMessageConstants.HOTPEPPAR_API_ERROR_CODE_9999);
	}
}
