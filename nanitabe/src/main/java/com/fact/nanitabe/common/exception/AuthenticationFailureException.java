package com.fact.nanitabe.common.exception;

/**
 * ログイン認証失敗例外クラス
 * @author k-sato
 *
 */
public class AuthenticationFailureException extends Exception {
	
	private static final long serialVersionUID = 1L; 

	// コンストラクタ
	public AuthenticationFailureException(){
	}
	public AuthenticationFailureException(String msg){
		super(msg);
	}
}
