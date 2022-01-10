/**
 * 
 */
package com.fact.nanitabe.domain.event;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.fact.nanitabe.common.property.MailProperties;

/**
 * @author k-sato
 *　イベントを検知して処理を開始するListenerクラス
 */
@Component
public class MailSendEventListener implements ApplicationListener<MailSendEvent> {

	@Autowired
	MailProperties mailProperties;
	
	private static final Logger logger = LogManager.getLogger(MailSendEventListener.class);
	
	@Override
	public void onApplicationEvent(MailSendEvent event) {

		logger.info("メール発行を開始");
		
		try {
			
			// 各設定値
			Properties property = new Properties();
			property.put("mail.smtp.host", "smtp.gmail.com");
			property.put("mail.smtp.auth", "true");
			property.put("mail.smtp.starttls.enable", "true");
			property.put("mail.smtp.host", "smtp.gmail.com");
			property.put("mail.smtp.port", "587");
			property.put("mail.smtp.debug", "true");

			// セッション
			Session session = Session.getInstance(property, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(event.getMailSendBean().getFrom(), mailProperties.getAccountPass());
				}
			});
			
			// メールコンテンツ取得
			String from = event.getMailSendBean().getFrom();
			String to = event.getMailSendBean().getTo();
			String title = event.getMailSendBean().getTitle();
			String message = event.getMailSendBean().getMessage();
			String errorMessage = event.getMailSendBean().getErrorMessage();
			
			// メール作成
			MimeMessage mimeMessage = new MimeMessage(session);
			// 送信先
			InternetAddress toAddress = new InternetAddress(to, "nanitabe運営者");
			mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
			//　送信元
			InternetAddress fromAddress = new InternetAddress(from, "nanitabe");
			mimeMessage.setFrom(fromAddress);
			// メールタイトル
			mimeMessage.setSubject(title, "ISO-2022-JP");
			// メール本文
			mimeMessage.setText(message + "エラー内容:" + errorMessage, "ISO-2022-JP");
			//　メール送信
			Transport.send(mimeMessage);
			
			logger.info("送信が完了しました。");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("メールの送信に失敗しました。");
		}
	}

}
