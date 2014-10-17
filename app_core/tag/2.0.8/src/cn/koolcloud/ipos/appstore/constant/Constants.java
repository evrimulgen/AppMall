package cn.koolcloud.ipos.appstore.constant;

import android.app.SearchManager;
import android.provider.BaseColumns;
import cn.koolcloud.ipos.appstore.R;

/**
 * <p>Title: ActivityConstants.java</p>
 * <p>Description: Some static constants used by activity stored.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2013-10-30
 * @version 	
 */
public class Constants {
	/**
	 * Passing serializable or parcelable params' key between activities or fragments 
	 */
	public  final static String SER_KEY = "com.koolcloud.ipos.appstore.ser";
	/**
	 * indicate app need update
	 */
	public final static int APP_NEW_VERSION_UPDATE 			= -1;
	/**
	 * indicate app is not installed need download and install 
	 */
	public final static int APP_NO_INSTALLED_DOWNLOAD 		= -2;
	/**
	 * indicate app is installed show open button
	 */
	public final static int APP_INSTALLED_OPEN 				= 0;
	/**
	 * indicate app is downloaded show install
	 */
	public final static int APP_DOWNLOADED_INSTALL 			= -4;
	/**
	 * show app update page while receiving notification
	 */
	public final static int TYPE_APP_UPDATE 				= 0;
	/**
	 * show app promotion page while receiving notification
	 */
	public final static int TYPE_NOTIFICATION_PROMOTION 	= 1;
	/**
	 * jump to app webview page while clicking promotion image
	 */
	public final static int TYPE_AD_TYPE_WEBVIEW 			= 1;
	/**
	 * jump to app details page while clicking promotion image
	 */
	public final static int TYPE_AD_TYPE_APP 				= 0;
	public final static String REG_PACKAGE_MATCH 	= "^(com.allinpay|cn.koolcloud).*$";
//	public final static String REG_HTTPS_MATCH 	= "^(https://)*$";
	public final static String REG_HTTPS_MATCH 	= "(?=https|/)[^']*";
	
	//use for search view suggestion adapter in action bar
	//just for test
	public static final String[] COLUMNS = {
        BaseColumns._ID,
        SearchManager.SUGGEST_COLUMN_TEXT_1,
	};
	
	public static final Integer NAV_ITEM_FIRST_PAGE 			= 0;					//first page item number in navigation bar
	public static final Integer NAV_ITEM_RANKING 				= 1;					//ranking item number in navigation bar
	public static final Integer NAV_ITEM_CATEGORY 				= 2;					//category item number in navigation bar
	public static final Integer NAV_ITEM_TOPIC 					= 3;					//topic item number in navigation bar
	public static final Integer NAV_ITEM_MANAGEMENT 			= 4;					//management item number in navigation bar
	
	public static final Integer NAV_ITEM_APP_SETTING 			= 5;					//app setting item number in navigation bar
	public static final Integer NAV_ITEM_DOWNLOAD_SETTING 		= 6;					//app install item number in navigation bar
	
	public static final String USRE_GRADE_INCHARGE						="1";
	public static final String USER_GRADE_OPERATOR						="2";
	
	
	@Deprecated
	public static final Integer[][] NAV_ITEM_ARRAY = {
		{R.drawable.home_off, R.drawable.home_on, R.string.home},
		{R.drawable.paihang_off, R.drawable.paihang_on, R.string.ranking},
		{R.drawable.fenlei_off, R.drawable.fenlei_on, R.string.category},
		{R.drawable.zhuanti_off, R.drawable.zhuanti_on, R.string.topic},
		{R.drawable.guanli_off, R.drawable.guanli_on, R.string.management}
	};
	
	/**
	 * searching key when pass the data to search page
	 */
	public static final String SEARCH_WORD_KEY							= "search_word_key";
	/**
	 * the default hash data when request getAppcategories interface
	 */
	public static final String CATEGORY_DEFAULT_HASH					= "0";
	/**
	 * key for passing selected category ListView position
	 */
	public static final String CATEGORY_LIST_POSITION					= "category_list_position";
	/**
	 * key for passing selected app ListView position
	 */
	public static final String APP_LIST_POSITION						= "app_list_position";
	
	//the following JSON keys are all keys from network interface (details on doc)
	public static final String JSON_KEY_ONE								= "1";
	public static final String JSON_KEY_TWO								= "2";
	public static final String JSON_KEY_THREE							= "3";
	public static final String JSON_KEY_FOUR							= "4";
	public static final String JSON_KEY_FIVE							= "5";
	public static final String JSON_KEY_ACTION							= "action";
	public static final String JSON_KEY_USER							= "user";
	public static final String JSON_KEY_STRATEGY						= "strategy";
	public static final String JSON_KEY_CLIENT							= "client";
	public static final String JSON_KEY_ID								= "id";
	public static final String JSON_KEY_SN								= "sn";
	public static final String JSON_KEY_APPS							= "apps";
	public static final String JSON_KEY_VERSION							= "version";
	public static final String JSON_KEY_VERSION_CODE					= "versionCode";
	public static final String JSON_KEY_VENDOR							= "vendor";
	public static final String JSON_KEY_SIZE							= "size";
	public static final String JSON_KEY_CATEGORIES						= "categories";
	public static final String JSON_KEY_APP_ID							= "appId";
	public static final String JSON_KEY_APP_IDS							= "appIds";
	public static final String JSON_KEY_DOWNLOAD_ID						= "downloadId";
	public static final String JSON_KEY_COMMENT							= "comment";
	public static final String JSON_KEY_COMMENTS						= "comments";
	public static final String JSON_KEY_SCORE							= "score";
	public static final String JSON_KEY_SCORES							= "scores";
	public static final String JSON_KEY_DETAILS							= "details";
	public static final String JSON_KEY_TOTAL							= "total";
	public static final String JSON_KEY_AVERAGE							= "average";
	public static final String JSON_KEY_DATE							= "date";
	public static final String JSON_KEY_NAME							= "name";
	public static final String JSON_KEY_DESC							= "desc";
	public static final String JSON_KEY_ICON							= "icon";
	public static final String JSON_KEY_PRIORITY						= "priority";
	public static final String JSON_KEY_DESCRIPTION						= "description";
	public static final String JSON_KEY_NEW_FEATURE_DES					= "appVersionDesc";
	public static final String JSON_KEY_CPU_INFO						= "cpuInfo";
	public static final String JSON_KEY_DISPLAY							= "display";
	public static final String JSON_KEY_MEM_INFO						= "memInfo";
	public static final String JSON_KEY_SCREENSHOT1						= "screenshot1";
	public static final String JSON_KEY_SCREENSHOT2						= "screenshot2";
	public static final String JSON_KEY_SCREENSHOT3						= "screenshot3";
	public static final String JSON_KEY_PRINTER_INFO					= "printerInfo";
	public static final String JSON_KEY_RESOLUTION_INFO					= "resolutionInfo";
	public static final String JSON_KEY_CARD_READER_INFO				= "cardReaderInfo";
	public static final String JSON_KEY_MANUFACTURER					= "manufacturer";
	public static final String JSON_KEY_CLIENT_VERSION					= "clientVersion";
	public static final String JSON_KEY_TERMINAL_ID						= "terminalId";
	public static final String JSON_KEY_TERMINAL_MODEL					= "terminalModel";
	public static final String JSON_KEY_DOWNLOAD_APK_URL				= "apk_url";
	public static final String JSON_KEY_PACKAGE_NAME					= "packageName";
	public static final String JSON_KEY_FEE								= "fee";
	public static final String JSON_KEY_BAIDU_PUSH						= "baiduPush";
	public static final String JSON_KEY_USER_ID							= "userId";
	public static final String JSON_KEY_CHANNEL_ID						= "channelId";
	public static final String JSON_KEY_TYPE							= "type";
	public static final String JSON_KEY_ANDROID_VERSION					= "android_version";
	public static final String JSON_KEY_PROMOTIONS						= "promotions";
	public static final String JSON_KEY_IMG								= "img";
	public static final String JSON_KEY_URL								= "url";
	public static final String JSON_KEY_TITLE							= "title";
	public static final String JSON_KEY_IMAGE							= "image";
	
	public static final String JSON_KEY_PAY_MENTS						= "payments";
	public static final String JSON_KEY_PAY_TYPE						= "payType";
	public static final String JSON_KEY_PAY_NAME						= "name";
	public static final String JSON_KEY_PAY_URL							= "url";
	public static final String JSON_KEY_PAY_OUT_TRADE_NO				= "outTradeNo";
	public static final String JSON_KEY_PAY_SUBJECT						= "subject";
	public static final String JSON_KEY_PAY_BODY						= "body";
	public static final String JSON_KEY_PAY_FEE							= "fee";
	public static final String JSON_KEY_PAYMENT_DATA					= "paymentData";
	public static final String JSON_KEY_SYNC_RESPONSE					= "paymentName";
	
	public static final String STRATEGY_UPDATE_FORCE					= "2";
	public static final String STRATEGY_UPDATE_OPTION					= "1";
	public static final String STRATEGY_UPDATE_NONE						= "0";
	
	public static final String REQUEST_DATA 							= "data";
	public static final String REQUEST_HASH 							= "hash";
	public static final String REQUEST_STATUS 							= "status";
	
	//response codes
	public static final String REQUEST_STATUS_OK 						= "200";
	public static final String REQUEST_STATUS_FORBIDDEN 				= "403";
	
	//应用收费
	public static final int PAY_TYPE_FREE								= 0;
	public static final int PAY_TYPE_NOT_FREE							= 1;
	public static final int PAY_TYPE_PROGRESS							= 2;
	//支付宝 返回值
	public static final  String REQUEST_DEAL_FINISH 					= "9000";
	
	//人民币符号常量
	public static final String RMB_UNIT = "¥";
	
	//下载相关常量
	public static final String ACTION_TASK_STARTED = "task_started";
    public static final String ACTION_TASK_PAUSED = "task_paused";
    public static final String ACTION_TASK_CANCEL = "task_removed";
    public static final String ACTION_TASK_UPDATED = "task_updated";
    public static final String ACTION_TASK_FINISHED = "task_finished";
    public static final String ACTION_TASK_ERROR = "task_error";
    public static final String ACTION_TASK_COMPLETE_PROCESS = "task_complete_process";
    public static final int DOWNLOAD_UNFINISHED = 0;
    public static final int DOWNLOAD_FINISHED = 1;
    public static final int INSERT_DB_FAILED_ID = -1;
    public static final String TASK_PACKAGE_NAME = "task_pkg_name";
    public static final String TASK_ERROR_MES = "task_error_mes";
    public static final String TASK_COMPLETE_PROCESS = "task_complete_process";
}
