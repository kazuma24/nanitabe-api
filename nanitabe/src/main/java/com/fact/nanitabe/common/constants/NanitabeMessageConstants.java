package com.fact.nanitabe.common.constants;

public class NanitabeMessageConstants {

	// 新規登録API メッセージ
	public static final String NEWREGISTER_MESSAGE_1 = "メールアドレスを入力してください。";
	public static final String NEWREGISTER_MESSAGE_2 = "メールアドレスはメール形式で入力してください。";
	public static final String NEWREGISTER_MESSAGE_3 = "登録済みのメールアドレスです。";
	public static final String NEWREGISTER_MESSAGE_4 = "パスワードを入力してください。";
	public static final String NEWREGISTER_MESSAGE_5 = "パスワードは、半角英数字の8文字以上20文字以内で入力してください。";
	public static final String NEWREGISTER_MESSAGE_6 = "パスワードを入力してください。";
	public static final String NEWREGISTER_MESSAGE_7 = "新規登録に成功しました";
	public static final String NEWREGISTER_MESSAGE_8 = "新規登録が失敗しました。再度お試しください。";
	
	// 詳細検索
	public static final String DETAILED_SEARCH_MESSAGE_400 = "検索条件が有効ではありません。";
	public static final String DETAILED_SEARCH_MESSAGE_400_1 = "検索に必要な情報がありません";
	
	public static final String SEARCH_FAILURE_MESSAGE = "検索が失敗しました。再度お試しください。";
	
	//　エラーコード
	public static final String HOTPEPPAR_API_ERROR_CODE_1000 = "1000"; //サーバー障害エラーコード
	public static final String HOTPEPPAR_API_ERROR_CODE_2000 = "2000"; //APIキー認証エラー
	public static final String HOTPEPPAR_API_ERROR_CODE_3000 = "3000"; //パラメータ不正
	public static final String HOTPEPPAR_API_ERROR_CODE_4000 = "4000"; //タイムアウト連続エラー
	public static final String HOTPEPPAR_API_ERROR_CODE_5000 = "5000"; // ｈｔｔｐ通信時何かしらの失敗
	
	public static final String HOTPEPPAR_API_ERROR_CODE_9998 = "9998"; // レスポンスjson解析失敗
	public static final String HOTPEPPAR_API_ERROR_CODE_9999 = "9999"; // 未定義のエラー
	
	// ログイン
	public static final String LOGIN_AUTH_FAILURE = "ログインに失敗しました。再度お試しください。";
	public static final String LOGIN_ACCESS_DENIED = "アクセスが拒否されました。";
	
	
	public static final String SYSTEM_ERROR = "システムエラーが発生しました。申し訳ございませんが再度お試しください。";
	
	//　お気に入り登録
	public static final String FAVORITE_SUCCESS = "%sをお気に入り登録しました。";
	public static final String FAVORITE_DUPLICATE = "お気に入り登録済みです。";
	public static final String FAVORITE_REGISTE_BAD_REQUEST = "お気に入り登録に必要な情報がありません。";
	public static final String FAVORITE_BAD_REQUEST_ID = "お気に入り登録ができません。無効なリクエストです。";
	// お気に入り削除
	public static final String FAVORITE_DELETE_SUCCESS = "お気に入りから削除しました。";
	public static final String FAVORITE_DELETE_ERROR = "お気に入り削除に失敗しました。";
	public static final String FAVORITE_DELETE_BAD_REQUEST = "お気に入り削除に必要な情報がありません。";
	public static final String FAVORITE_DELETE_BAD_REQUEST_ID = "お気に入り削除ができません。無効なリクエストです。";
	
	
}
