package com.fact.nanitabe.common.constants;

public class NanitabeLogConstants {

	// JWT
	public static final String JWTPROVIDER_INFO_1 = "JWTトークンの生成します。";
	public static final String JWTPROVIDER_INFO_2 = "JWTトークンをの生成が完了しました。";
	public static final String JWTPROVIDER_INFO_3 = "JWTトークンは有効です。";
	
	// JWTトークン検証APIのログ
	public static final String JWTPROVIDER_ERROR_1 = "JwtToken: {} は無効なトークンです";
	public static final String JWTPROVIDER_ERROR_2 = "JwtToken: {} はクレーム(ペイロード)に予想とは異なる値が含まれています";
	public static final String JWTPROVIDER_ERROR_3 = "JwtToken: {} はトークンの有効期限が切れています。";
	public static final String JWTPROVIDER_ERROR_4 = "JwtToken: {} の署名は無効です";
	public static final String JWTPROVIDER_ERROR_5 = "JwtToken: {} のアルゴリズムが定義されているものと等しくないです。";
	public static final String JWTPROVIDER_ERROR_6 = "JwtToken: {} のデコードに失敗しました。";
	
	// 新規登録API INFO
	public static final String NEWREGISTER_INFO_1 = "新規登録API : パラメーター【email】は正常です。";
	public static final String NEWREGISTER_INFO_2 = "新規登録API : 新規登録を実施します。";
	public static final String NEWREGISTER_INFO_3 = "新規登録API : 新規登録は正常に終了しました。";
	public static final String NEWREGISTER_INFO_4 = "新規登録API : パラメーター【password】は正常です。";
	
			
	// 新規登録API ERROR
	public static final String NEWREGISTER_ERROR_1 = "新規登録API : 必須パラメーターがありません。";
	
	public static final String NEWREGISTER_ERROR_3 = "新規登録API ： 無効または不適切なアルゴリズム・パラメータです。現在の環境では使用できません。 {} ";
	public static final String NEWREGISTER_ERROR_4 = "新規登録API ： 新規登録に失敗しました。 {} ";
	public static final String NEWREGISTER_ERROR_5 = "新規登録API ： email: {} は登録済みです。";
	
	// DB INFO
	public static final String USERS_TRANZACTION_INFO_1 = "Usersテーブル : メールアドレス: {} のレコードは存在しません。";
	
	// DBエラー
	public static final String USERS_TRANZACTION_SELECT_ERROR_1 = "Usersテーブル : DB接続に失敗しました。 {} ";
	public static final String USERS_TRANZACTION_SELECT_ERROR_2 = "Usersテーブル : emailが複数登録されています。 {} ";
	public static final String USERS_TRANZACTION_INSERT_ERROR_1 = "Usersテーブル : レコード登録に失敗しました。 {} ";
	
	public static final String FAVOS_TRANZACTION_INSERT_ERROR = "Favosテーブル : レコード登録に失敗しました。 {}";
	public static final String FAVOS_TRANZACTION_DELETE_ERROR = "Favosテーブル : レコード削除に失敗しました。 {}";
	
	// ホットペッパーAPI ERROR
	public static final String HOTPEPPAR_API_ERROR_1 = "ホットペッパーAPI : ステータスが200以外が返りました。 ステータスコード: {}";
	public static final String HOTPEPPAR_API_TIMEOUT_ERROR = "ホットペッパーAPI : タイムアウトました。";
	public static final String HOTPEPPAR_API_TIMEOUT_ERROR_1 = "ホットペッパーAPI : タイムアウトました。　リトライ回数 {}";
	public static final String HOTPEPPAR_API_SERVER_ERROR = "ホットペッパーAPI : API通信に失敗しました。 {}";
	public static final String HOTPEPPAR_API_ERROR_1000 = "ホットペッパーAPI :サーバ障害エラーです。 エラーコード: {} メッセージ: {}";
	public static final String HOTPEPPAR_API_ERROR_2000 = "ホットペッパーAPI :APIキーまたはIPアドレスの認証エラーです。 エラーコード: {} メッセージ: {}";
	public static final String HOTPEPPAR_API_ERROR_3000 = "ホットペッパーAPI :パラメータ不正エラーです。 エラーコード: {} メッセージ: {}";
	public static final String HOTPEPPAR_API_ERROR_4000 = "ホットペッパーAPI : タイムアウトが連続したためリクエストを終了します。";
	public static final String HOTPEPPAR_API_ERROR_9999 = "ホットペッパーAPI :未定義のエラーです。 エラーコード: {}";
	public static final String HOTPEPPAR_LOGIC_ERROR_1 = "レスポンスのjson解析失敗しました。";
	
    // ホットペッパーAPI　INFO
	public static final String HOTPEPPAR_API_INFO_1 = "ホットペッパーAPI : 取得件数が10件未満のため、検索範囲を広げて再度リクエストをします。 リクエスト回数: {} ,取得件数: {} ";
	public static final String HOTPEPPAR_API_INFO_2 = "ホットペッパーAPI : 取得件数が10件未満ですが、検索範囲が最大のためリクエストを終了します。 取得件数: {}";
	public static final String HOTPEPPAR_API_INFO_3 = "ホットペッパーAPI : 取得件数が10件以上のため、リクエストを終了します。 取得件数: {}";
	public static final String HOTPEPPAR_API_INFO = "HotPepparAPIに通信します。 リクエストURL： {}";
	
	// ログイン
	public static final String LOGIN_AUHT_ERROR_1 = "ログインAPI : ログインに失敗しました。emailおよびpasswordが一致しません。 email {} ";
	public static final String LOGIN_AUHT_ERROR_2 = "ログインAPI : ログインに失敗しました。JWTトークンのuserIdでDBにデータがありません userId {} ";
	public static final String LOGIN_AUTH_ERROR_3 = "ログインAPI : システムエラーが発生しました。　エラー内容: {}";
	public static final String LOGIN_AUHT_ERROR_4 = "ログインAPI : リクエストのemailでDBにデータがありません email {} ";
	
	public static final String LOGIN_INFO_1 = "ログインに成功しました。";
	
	//お気に入り登録
	public static final String FAVORITE_DUPLICATE_INFO= "お気に入り登録API: お気に入り登録済みです。";
	public static final String FAVORITE_SYSTEM_ERROR= "お気に入り登録API: システムエラーが発生しました。　エラー内容: {}";
	
	public static final String FAVORITE_GET_SYSTEM_ERROR= "お気に入り取得API: システムエラーが発生しました。　エラー内容: {}";
	
	public static final String JSON_NODE_PARSE_ERROR = "レスポンスのjsonのパース処理に失敗しました。　{}";
	
	//　共通
	public static final String PROCESS_START = "{}処理を開始します。";
	
}
