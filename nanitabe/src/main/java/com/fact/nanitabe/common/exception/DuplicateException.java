/**
 * 
 */
package com.fact.nanitabe.common.exception;

/**
 * @author k-sato
 * DB重複時用エラークラス
 */
public class DuplicateException extends Exception {
	
	private static final long serialVersionUID = 1L; 

	// コンストラクタ
	public DuplicateException(){
	}
	public DuplicateException(String msg){
		super(msg);
	}
}
