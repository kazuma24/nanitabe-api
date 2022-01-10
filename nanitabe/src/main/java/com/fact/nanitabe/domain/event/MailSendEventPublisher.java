/**
 * 
 */
package com.fact.nanitabe.domain.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author k-sato
 * メール送信イベントを起動させるPublisherクラス
 */
@Component
@Async
public class MailSendEventPublisher {
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	public void ignite(String from, String to, String title, String message, String errorMessage) {
		//　メール作成
		MailSendBean mailSend = MailSendBean.of(from, to, title, message,errorMessage);
        // イベント作成する
		MailSendEvent event = new MailSendEvent(this, mailSend);
        // イベント発行
        applicationEventPublisher.publishEvent(event);
    }
}
