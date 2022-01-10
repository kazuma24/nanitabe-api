/**
 * 
 */
package com.fact.nanitabe.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author k-sato
 * 処理実行に必要なパラメータ類を渡すEventクラス
 */
public class MailSendEvent extends ApplicationEvent {

	private final MailSendBean mailSendBean;
	
	public MailSendEvent(Object source, MailSendBean mailSendBean) {
		super(source);
		this.mailSendBean = mailSendBean;
	}

	public MailSendBean getMailSendBean() {
		return mailSendBean;
	}

}
