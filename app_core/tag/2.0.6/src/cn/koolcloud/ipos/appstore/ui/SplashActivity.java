package cn.koolcloud.ipos.appstore.ui;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.koolcloud.ipos.appstore.BaseActivity;
import cn.koolcloud.ipos.appstore.MainActivity;
import cn.koolcloud.ipos.appstore.MyApp;
import cn.koolcloud.ipos.appstore.R;
import cn.koolcloud.ipos.appstore.api.ApiService;
import cn.koolcloud.ipos.appstore.constant.Constants;
import cn.koolcloud.ipos.appstore.entity.ResultSet;
import cn.koolcloud.ipos.appstore.interfaces.CallBack;
import cn.koolcloud.ipos.appstore.utils.JsonUtils;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.ipos.appstore.utils.MySPEdit;
import cn.koolcloud.ipos.appstore.utils.NetUtil;
import cn.koolcloud.ipos.appstore.utils.PushUtils;
import cn.koolcloud.ipos.appstore.utils.ToastUtil;
import cn.koolcloud.pos.service.IMerchService;
import cn.koolcloud.pos.service.ISecureService;

import com.baidu.android.pushservice.PushManager;

public class SplashActivity extends BaseActivity {
	public static final String NETWORK_AVAILABLE_KEY = "is_network_available";
	private final int CURRENT_PAGE_SLEEP_TIME = 2500;
	private static final int SHOW_TOAST = 1;
	private static final int GO_TO_MAIN_PAGE = 2;
	private boolean isAIDLSuccess;
	private UserBean userBean = new UserBean();
	private MyApp myApp;
	
	protected IMerchService mIMerchService;
    private ServiceConnection merchConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIMerchService = IMerchService.Stub.asInterface(service);
			Log.i("Client", "Bind mMerchService Success:" + mIMerchService.getClass().toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIMerchService = null;
		}
	};
	
	protected ISecureService mISecureService;
	private ServiceConnection secureConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mISecureService = ISecureService.Stub.asInterface(service);
			Log.i("Client", "Bind mSecureService Success:" + mISecureService.getClass().toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mISecureService = null;
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		setContentView(R.layout.loading);
		isAIDLSuccess = true;
		myApp = (MyApp) getApplication();
		boundServices();

		// 延迟500毫秒是为了给绑定service一个时间
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				if (mISecureService != null) {
					try {
						String userInfo = mISecureService.getUserInfo();
						JSONObject jsObj = new JSONObject(userInfo);
						MyLog.e("operator:" + jsObj.optString("userName") + " gradeId:" + jsObj.optString("gradeId") + " userStatus:" + jsObj.optString("userStatus"));
						userBean.setUserName(jsObj.optString("userName"));
                		userBean.setGradeId(jsObj.optString("gradeId"));
                		userBean.setUserStatus(jsObj.optString("userStatus"));
					} catch (Exception e) {
						e.printStackTrace();
	                   // isAIDLSuccess = false; 
					}
				}
				
				if (mIMerchService != null) {
					try {
						String merchName = mIMerchService.getMerchInfo().getMerchName();
						String merchId = mIMerchService.getMerchInfo().getMerchId();
						String terminalId = mIMerchService.getMerchInfo().getTerminalId();
						
						MyLog.e("merchName:" + merchName + " merchId:" + merchId + " terminalId:" + terminalId);
					
						userBean.setMerchName(merchName);
                		userBean.setMerchId(merchId);
                		userBean.setTerminalId(terminalId);
                		
                		if(isAIDLSuccess) { 
                    		// success，跳转到主页
	                		myApp.setUserBean(userBean);
	                		checkNetwork();
	                		boolean isOpenPush = MySPEdit.getPushNotificationTag(application);
	                		if (isOpenPush) {
	                			
	                			PushUtils.logStringCache = PushUtils.getLogText(getApplicationContext());
	                			PushUtils.loginBaiduCloud(getApplicationContext());
	                		} else {
	                			PushManager.stopWork(getApplicationContext());
	                		}
                		} else {
                			showLoginErrorDialog();
                		}
					} catch (Exception e) {
						e.printStackTrace();
						myApp.setUserBean(userBean);
						checkNetwork();
						//showLoginErrorDialog();
					}
				}else{
					myApp.setUserBean(userBean);
					checkNetwork();
				}
			}
		}, 500);
	}
	
	/**
	* @Title: checkNetwork
	* @Description: check network
	* @param 
	* @return void 
	* @throws
	*/
	private void checkNetwork() {
		//network is ok
		if (NetUtil.isAvailable(getApplicationContext())) {
			String terminalId = MySPEdit.getTerminalID(getApplicationContext());
			if (TextUtils.isEmpty(terminalId)) {
				String userId = MySPEdit.getUserID(getApplicationContext());
				String channelId = MySPEdit.getChannelId(getApplicationContext());
				registerClient(userId, channelId);
			} else {
				new JumpToMainActivity().start();
			}
		} else {
			ToastUtil.showToast(getApplicationContext(), R.string.dialog_network_not_available, Toast.LENGTH_LONG);
			new JumpToMainActivity().start();
		}
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				Toast.makeText(SplashActivity.this, msg.arg1, Toast.LENGTH_SHORT).show();
				break;
			case GO_TO_MAIN_PAGE:
				gotoMainActivity();
				break;
			}
			return false;
		}
	});
	
	public void registerClient(String userId, String channelId) {
		ApiService.register(this, userId, channelId, registerCallBack);
	}
	
	private CallBack registerCallBack = new CallBack() {
		@Override
		public void onCancelled() {
		}

		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(JSONObject jsonObj) {
			try {
				String retCode = "";
				MyLog.d("-------getRegisterInfo=" + jsonObj.toString());
				
				retCode = JsonUtils.getStringValue(jsonObj, Constants.REQUEST_STATUS);
				
				if (retCode.equals(Constants.REQUEST_STATUS_FORBIDDEN)) {
					ToastUtil.showToast(SplashActivity.this, R.string.msg_pos_forbiden, true);
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							exit();
						}
					}, 2000);
					return;
				}
				JSONObject dataJson = JsonUtils.getJSONObject(jsonObj, Constants.REQUEST_DATA);
				String terminalId = JsonUtils.getStringValue(dataJson, Constants.JSON_KEY_TERMINAL_ID);
				
				MySPEdit.saveTerminaID(getApplicationContext(), terminalId);
				new JumpToMainActivity().start();
			} catch (Exception e) {
				onFailure("get client register response error!");
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(String msg) {
			MyLog.d("describe=" + msg);
			if (msg.contains(ResultSet.NET_ERROR.describe)) {
				new JumpToMainActivity().start();
				ToastUtil.showToast(getApplicationContext(), R.string.nonetwork_prompt_server_error, Toast.LENGTH_LONG);
			}
		}
	};

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {   
            android.os.Process.killProcess(android.os.Process.myPid());
        }
  
        return super.onKeyDown(keyCode, event);
	}
	
	/**
	* @Title: gotoMainActivity
	* @Description: go to main page
	* @param 
	* @return void 
	* @throws
	*/
	private void gotoMainActivity() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//		Bundle translateBundle = ActivityOptions.makeCustomAnimation(
//				SplashActivity.this,
//				android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
		startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		SplashActivity.this.finish();
	}
	
	class JumpToMainActivity extends Thread {

		@Override
		public void run() {
			mHandler.sendEmptyMessageDelayed(GO_TO_MAIN_PAGE, CURRENT_PAGE_SLEEP_TIME);
		}
	}
	
	private void boundServices() {
		
		try{
			Intent mserchService = new Intent(IMerchService.class.getName());
			bindService(mserchService, merchConnection, BIND_AUTO_CREATE);
        
			Intent secureService = new Intent(ISecureService.class.getName());
			bindService(secureService, secureConnection, BIND_AUTO_CREATE);
		}catch(SecurityException e){
			e.printStackTrace();
			//TODO just for test that 绑定失败 一种可能情况，远程service apk升级，本程序继续绑定久的service
			secureConnection = null;
			Toast.makeText(myApp, "绑定失败", 5000).show();
		}
	}
	
	private void showLoginErrorDialog() {
		new AlertDialog.Builder(SplashActivity.this).setTitle(R.string.hint)
			.setMessage(R.string.login_error)
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					SplashActivity.this.finish();
				}
			})
			.setCancelable(false)
			.show();
	}
	
	@Override
	protected void onDestroy() {
		unboundServices();
		super.onDestroy();
	}

	private void unboundServices() {
		if(merchConnection != null)
			unbindService(merchConnection);
		if(secureConnection != null)
			unbindService(secureConnection);
	}
}
