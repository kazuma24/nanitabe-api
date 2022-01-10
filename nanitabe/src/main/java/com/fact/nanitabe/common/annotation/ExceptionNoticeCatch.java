package com.fact.nanitabe.common.annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fact.nanitabe.common.property.MailProperties;
import com.fact.nanitabe.domain.event.MailSendEventPublisher;

/**
 * 未定義のエラーが発生した場合の処理
 * @author k-sato
 * ログ出力
 * インシデントメール発行
 */
@Aspect
@Component
public class ExceptionNoticeCatch {
	
	@Autowired
	MailProperties mailProperties;
	
	@Autowired
	MailSendEventPublisher mailSendPublisher;
	
	private static final Logger logger = LogManager.getLogger(ExceptionNoticeCatch.class);
	
	@AfterThrowing(value = "@annotation(com.fact.nanitabe.common.annotation.ExceptionNotice)", throwing="e")
	public void throwingCatch(JoinPoint jp, Exception e) {
		logger.error("未定義のエラーが発生しました。" + e.toString());
		// インシデントメール送信
		mailSendPublisher.ignite(
				mailProperties.getFrom(), 
				mailProperties.getAdmin(),
				mailProperties.getTitleNotice(), 
				mailProperties.getMessageNotice(),
				e.toString()
				);
	}
}
