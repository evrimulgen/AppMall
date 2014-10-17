package cn.koolcloud.ipos.appstore;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.koolcloud.ipos.appstore.adapter.GeneralAppsListAdapter;
import cn.koolcloud.ipos.appstore.fragment.AppDetailLeftFragment;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.ipos.appstore.utils.ToastUtil;

import com.actionbarsherlock.view.Menu;

public class PayWebViewActivity extends BaseActivity {

	private WebView webView;
	private String url = "";
	private final static String URL = "https://appstore.koolyun.com/asapi";//"http://appstore.koolyun.cn:10000/asapi";
	//private final static String URL = "http://appstore.koolyun.cn:10000/asapi";
	private int position = -1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setBackgroundDrawableResource(R.color.pay_bg);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);  
		setContentView(R.layout.webview_main);
		//actionBar = getSupportActionBar();
		
		getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON); 
		url = getIntent().getStringExtra("url");
		position = getIntent().getIntExtra("position", -1);
		MyLog.i("webViewActivity===="+url);
		findViews();
		readHtmlFromAssets();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();// remove session.
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void findViews() {
		
//		actionBar.setDisplayHomeAsUpEnabled(false);
//      actionBar.setDisplayUseLogoEnabled(false);
//      actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bg));
//      actionBar.setIcon(R.drawable.logo);
//      actionBar.setTitle(" "+getResources().getString(R.string.app_name));
//      get webview components
		webView = (WebView) findViewById(R.id.webview);
		// allow javascript
		webView.getSettings().setJavaScriptEnabled(true);
		webView.requestFocus();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void readHtmlFromAssets() {
		WebSettings webSettings = webView.getSettings();

		webSettings.setSupportZoom(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
 		webSettings.setJavaScriptEnabled(true);
 		//webSettings.setLoadsImagesAutomatically(true);
		
		//webView.setBackgroundColor(Color.TRANSPARENT); // set WebView background
														// color TRANSPARENT
		webView.setWebViewClient(new MyWebView());
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				PayWebViewActivity.this.setTitle("Loading...");  
				// Activity and webview decide progression, the progress
				// disappear when 100% loaded
				PayWebViewActivity.this.setProgress(progress * 100);
				if(progress == 100)
					PayWebViewActivity.this.setTitle("");  
			}
		});

		webView.addJavascriptInterface(new JavaScriptInterface(), "android");
		// load html files from assets
		// webView.loadUrl("file:///android_asset/html/index.html");
		// webView.loadUrl(url + ":" + port);
		// webView.loadUrl("http://blog.csdn.net");
		//"http://appstore.koolyun.cn:10000/asapi/alipay/callBack/870?out_trade_no=201409171605000343&request_token=requestToken&result=success&sign=185933f296a15c4bdceb2eefb842524a&sign_type=MD5&trade_no=2014091760441086"
		webView.loadUrl(URL+url);
	}
	
	Handler mHandler = new Handler();

	/**
	 * @author Administrator deal with clicking hyper link
	 */
	class MyWebView extends WebViewClient {

		// override shouldOverrideUrlLoading method, avoid to open other
		// browsers when click the hyper link
		
		
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//			view.loadUrl(url);
//			// return true if you don't want to deal the click event on link,
//			// otherwise return false
//			return true;
//		}
	}
	
	final class JavaScriptInterface {
        JavaScriptInterface() {}
  
        /** 
         * this method is invoked by javascript 
         */
        @JavascriptInterface
        public void payResult(final String jsonObj) {
        	MyLog.i("WebView data from js====="+jsonObj);
        	if(jsonObj == null){ 
        		ToastUtil.showToast(PayWebViewActivity.this, R.string.pay_result, true);
        		return;
        	}
        	
        	try {
				JSONObject jsonObject = new JSONObject(jsonObj);
				String result = jsonObject.getString("result");
				String message = jsonObject.getString("message");
				MyLog.i("WebView data from js===result=="+result+"==message=="+message);
				Intent intent = new Intent();
            	Bundle bundle = new Bundle();
            	bundle.putInt("position", position);
            	if(result.equals("success")){
            		bundle.putBoolean("result", true);
            	}else{
            		bundle.putBoolean("result", false);
            	}
            	bundle.putString("message", message);
            	intent.putExtras(bundle);
            	if(position >=0)
            		setResult(GeneralAppsListAdapter.HANDLE_PAY_CHECK, intent);
            	else
            		setResult(AppDetailLeftFragment.HANDLE_CHECK_PAYMENT, intent);
            	finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}
            
        }
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
			webView.goBack();
			return true;
//			setResult(1, null);
//			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
