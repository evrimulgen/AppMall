package cn.koolcloud.ipos.appstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.koolcloud.ipos.appstore.adapter.GeneralAppsListAdapter;
import cn.koolcloud.ipos.appstore.api.ApiService;
import cn.koolcloud.ipos.appstore.constant.Constants;
import cn.koolcloud.ipos.appstore.download.multithread.MultiThreadService;
import cn.koolcloud.ipos.appstore.entity.App;
import cn.koolcloud.ipos.appstore.entity.AppInfo;
import cn.koolcloud.ipos.appstore.fragment.LocalSoftwareManagerFragment;
import cn.koolcloud.ipos.appstore.fragment.MainLeftFragment;
import cn.koolcloud.ipos.appstore.fragment.MainLeftFragment.OnTabChangedListener;
import cn.koolcloud.ipos.appstore.fragment.UpdateSoftwareFragment;
import cn.koolcloud.ipos.appstore.interfaces.CallBack;
import cn.koolcloud.ipos.appstore.receiver.MyDownloadReceiver;
import cn.koolcloud.ipos.appstore.ui.UserBean;
import cn.koolcloud.ipos.appstore.utils.Env;
import cn.koolcloud.ipos.appstore.utils.JsonUtils;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.ipos.appstore.utils.MySPEdit;
import cn.koolcloud.ipos.appstore.utils.ToastUtil;
import cn.koolcloud.pos.service.IMerchService;
import cn.koolcloud.pos.service.ISecureService;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.widget.SearchView;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener,
		SearchView.OnSuggestionListener, OnTabChangedListener {
	private long exitTime = 0;
	private static final int EXIT_LAST_TIME = 2000;
	private ActionBar actionBar;					//action bar
	private boolean useLogo = false;				//action bar logo
    private boolean showHomeUp = false;				//show home up in action bar
    private View customView;						//show title ,merchantName
	private static FragmentManager fragmentManager = null;
	private final String installedTabStr = "installed_tab";
	private final String canUpdateTabStr = "can_update_tab";
	private static MainActivity instance;
	private MyDownloadReceiver myDownlaodReceover;
	private MultiThreadService.IBinderImple binder;
	public static HashMap<String, Integer> downMap = new HashMap<String, Integer>();
	public static List<App> updateAppLits = new ArrayList<App>();
	public static HashMap<String, HashMap<String, String>> notifMap = new HashMap<String, HashMap<String, String>>();
	private boolean isAIDLSuccess;
	private UserBean userBean = new UserBean();
	private MyApp myApp;
	private String[] unkonwLoginArray = null;
	
    public MultiThreadService.IBinderImple getBinder() {
		return binder;
	}

	private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
        	MyLog.d("onServiceConnected");
            binder = (MultiThreadService.IBinderImple) service;
        }

        public void onServiceDisconnected(ComponentName name) {
        	MyLog.d("onServiceDisconnected");
        }
    };
    
	private IMerchService mIMerchService;
	private ServiceConnection merchConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIMerchService = IMerchService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIMerchService = null;
		}
	};

	private ISecureService mISecureService;
	private ServiceConnection secureConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mISecureService = ISecureService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mISecureService = null;
		}
	};
	
	public static MainActivity getInstance() {
		return instance;
	}
	
	private CallBack getUpdateCallBack = new CallBack() {
		@Override
		public void onCancelled() {
		}

		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(JSONObject jsonObj) {
			MyLog.e("可更新app：\n"+jsonObj);
			try {
				updateAppLits = JsonUtils.parseSearchingJSONApps(jsonObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(String msg) {
			MyLog.d("describe=" + msg);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		
		unkonwLoginArray = getResources().getStringArray(R.array.unknow_login_array);
		myDownlaodReceover = new MyDownloadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_TASK_STARTED);
		filter.addAction(Constants.ACTION_TASK_PAUSED);
		filter.addAction(Constants.ACTION_TASK_FINISHED);
		filter.addAction(Constants.ACTION_TASK_UPDATED);
		filter.addAction(Constants.ACTION_TASK_ERROR);
		registerReceiver(myDownlaodReceover, filter);
		
		setContentView(R.layout.main);
		fragmentManager = getSupportFragmentManager();
		activityList.add(this);
		initActionBar();
		
		initFragments();
		getClientVersion();
		
		isAIDLSuccess = true;
		myApp = (MyApp) getApplication();
		boundServices();
		
		Intent intent = new Intent(this, MultiThreadService.class);
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
		
		List<AppInfo> localSoftDataSource = Env.getInstalledAppsToList(application, false);
		ApiService.checkAllAppUpdate(this, MySPEdit.getTerminalID(this), localSoftDataSource, getUpdateCallBack);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				Bundle bundle = getIntent().getExtras(); 
				if(bundle != null) {
					String name = bundle.getString("NAME");
					if(name !=null && name.equals("MSCService")) {
			    		Intent mIntent = new Intent(getApplicationContext(), AppDetailActivity.class);
						mIntent.putExtras(bundle);
						startActivity(mIntent);	
					}
				}
			}
		};
		new Handler().postDelayed(r, 500);
	}
	
	private void initFragments() {
		//navigation bar
		MainLeftFragment navFragment = MainLeftFragment.getInstance();
		
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.replace(R.id.left_content, navFragment);
		fragTransaction.commitAllowingStateLoss();
	}
	
	public void refreshLocalSoftData() {
		((MyApp) application).initApps();
		
		List<AppInfo> list = Env.getInstalledAppsToList(application, false);
		try {
			Fragment f = fragmentManager.findFragmentById(R.id.frame_content);
			if(f instanceof LocalSoftwareManagerFragment) {
				((LocalSoftwareManagerFragment)f).refreshData(list);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			((TextView) (actionBar.getSelectedTab().getCustomView().findViewById(R.id.titleImage)))
					.setText(list.size() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshInstalledAppNum(int num) {
		try {
			((TextView) (actionBar.getSelectedTab().getCustomView().findViewById(R.id.titleImage)))
					.setText(num + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initActionBar() {
		actionBar = getSupportActionBar();

        // set defaults for logo & home up
        actionBar.setDisplayHomeAsUpEnabled(showHomeUp);
        actionBar.setDisplayUseLogoEnabled(useLogo);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bg));
        
        actionBar.setIcon(R.drawable.logo);
        actionBar.setTitle(" "+getResources().getString(R.string.app_name));
        //showTabsNav();
        showCustom();
	}
	
	private void setupTabs() {
		Tab installedTab = actionBar.newTab();
		installedTab.setTag(installedTabStr);
		installedTab.setTabListener(new TabSelectListener());
		View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.actionbar_tab, null);
		((TextView) view1.findViewById(R.id.titleText)).setText(R.string.installed);
		((TextView) view1.findViewById(R.id.titleImage)).setText("0");
		((TextView) view1.findViewById(R.id.titleImage)).setBackgroundResource(R.drawable.install_tab);
		installedTab.setCustomView(view1);
		actionBar.addTab(installedTab);
		
		Tab canUpdatedTab = actionBar.newTab();
		canUpdatedTab.setTag(canUpdateTabStr);
		canUpdatedTab.setTabListener(new TabSelectListener());
		View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.actionbar_tab, null);
		((TextView) view2.findViewById(R.id.titleText)).setText(R.string.can_update);
		if(updateAppLits == null) {
			updateAppLits = new ArrayList<App>();
		}
		((TextView) view2.findViewById(R.id.titleImage)).setText(updateAppLits.size() + "");
		((TextView) view2.findViewById(R.id.titleImage)).setBackgroundResource(R.drawable.update_tab);
		canUpdatedTab.setCustomView(view2);
		actionBar.addTab(canUpdatedTab);
	}
	
	public void showTabsNav() {
        if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
        	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }
	
	private void showCustom(){
		if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
        	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        	//actionBar.setDisplayShowCustomEnabled(true);
        	refreshUserInfor();
		}
	}
	
	private void refreshUserInfor(){
		customView = mInflater.inflate(R.layout.actionbar_custom_layout, null);
    	TextView actionBarMerchantName = (TextView) customView.findViewById(R.id.actionBarMerchantName);
    	TextView actionBarUserName = (TextView) customView.findViewById(R.id.actionBarUserName);
    	UserBean userBean = application.getUserBean();
    	if(userBean == null){
    		userBean = new UserBean();
    		userBean.setGradeId("2");
    		userBean.setMerchName(unkonwLoginArray[0]);  
    	}
    	actionBarMerchantName.setText(userBean.getMerchName());
    	Integer gradeId = null; 
    	if(userBean.getGradeId() != null && userBean.getGradeId().length() != 0){
    		gradeId = Integer.parseInt(userBean.getGradeId());
    		actionBarUserName.setText(userBean.getUserName() +"  " +(gradeId == 1 ? getResources().getString(R.string.user_manager) : getResources().getString(R.string.user_cashier)));
    	}
    	
    	if(userBean.getUserName() != null &&userBean.getUserName().equals(unkonwLoginArray[0]))
    		actionBarUserName.setText(unkonwLoginArray[2]);
        actionBar.setCustomView(customView,new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
	}
	
	public class TabSelectListener implements ActionBar.TabListener {
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			String selectedTabTag = String.valueOf(tab.getTag());
			
			//otherwise the programe will throw "commit already called" exception when add the tabs
			FragmentTransaction tran = fragmentManager.beginTransaction();
			if  (selectedTabTag.equals(installedTabStr))  {
				LocalSoftwareManagerFragment localSoftFragment = LocalSoftwareManagerFragment.getInstance();
				tran.replace(R.id.frame_content, localSoftFragment);
				tran.commitAllowingStateLoss();
			}  else if  (selectedTabTag.equals(canUpdateTabStr))  {
				UpdateSoftwareFragment updateSoftFragment = UpdateSoftwareFragment.getInstance();
				tran.replace(R.id.frame_content, updateSoftFragment);
				tran.commitAllowingStateLoss();
			}
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		UpdateSoftwareFragment updateSoftFragment = (UpdateSoftwareFragment)
				fragmentManager.findFragmentById(R.id.frame_content);
		if (updateSoftFragment != null) {
			updateSoftFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
		    if ((System.currentTimeMillis() - exitTime) > EXIT_LAST_TIME) {
		    	ToastUtil.showToast(getApplicationContext(), R.string.msg_exist_toast);
		        exitTime = System.currentTimeMillis();
			} else {
				exit();
			}
		    return true;
	    }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void disableAllTabs() {
		actionBar.removeAllTabs();
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void enableAllTabs() {
		setupTabs();
		if(actionBar.getCustomView() != null){
    		//actionBar.getCustomView().setVisibility(View.GONE);
    		actionBar.setDisplayShowCustomEnabled(false);
    	}
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		unboundServices();
		if(downMap != null && downMap.size() > 0) {
			downMap.clear();
		}
		if(updateAppLits != null && updateAppLits.size() > 0) {
			updateAppLits.clear();
		}
		try {
			unregisterReceiver(myDownlaodReceover);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final NotificationManager manager = (NotificationManager)
				getSystemService(Context.NOTIFICATION_SERVICE);
		final Iterator<String> iter = notifMap.keySet().iterator();

		while (iter.hasNext()) {
			HashMap<String, String> tmpM = notifMap.get(iter.next());
			int id = Integer.parseInt(tmpM.keySet().iterator().next());
			manager.cancel(id);
		}
		if(notifMap != null && notifMap.size() > 0) {
			notifMap.clear();
		}
		GeneralAppsListAdapter.recordDownMap.clear();
		super.onDestroy();
	}

	public void refreshUpdateSoftData() {
		if(updateAppLits != null && updateAppLits.size() > 0) {
			List<App> tmpList = new ArrayList<App>();
			List<AppInfo> list = Env.getInstalledAppsToList(application, false);
			for (AppInfo appInfo : list) {
				for (App app : updateAppLits) {
					if(app.getPackageName().equals(appInfo.getPackageName()) &&
							app.getVersionCode() == appInfo.getVersionCode()) {
						tmpList.add(app);
					}
				}
			}
			updateAppLits.removeAll(tmpList);
			
			try {
				((TextView) (actionBar.getSelectedTab().getCustomView().findViewById(R.id.titleImage)))
						.setText(updateAppLits.size() + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Fragment f = fragmentManager.findFragmentById(R.id.frame_content);
		if(f instanceof UpdateSoftwareFragment) {
			((UpdateSoftwareFragment)f).refreshDataSet();
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
			Toast.makeText(myApp, "远程服务绑定失败", 5000).show();
		}
	}
	
	private void unboundServices() {
		if(merchConnection != null)
			unbindService(merchConnection);
		if(secureConnection != null)
			unbindService(secureConnection);
	}
	
	private void showLoginErrorDialog() {
		new AlertDialog.Builder(MainActivity.this).setTitle(R.string.hint)
			.setMessage(R.string.login_error)
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					MainActivity.this.finish();
				}
			})
			.setCancelable(false)
			.show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// 延迟500毫秒是为了给绑定service一个时间
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				userBean = new UserBean();
				if (mISecureService != null) {
					try {
						String userInfo = mISecureService.getUserInfo();
						JSONObject jsObj = new JSONObject(userInfo);
						MyLog.e("operator:" + jsObj.optString("userName")
								+ " gradeId:" + jsObj.optString("gradeId")
								+ " userStatus:"
								+ jsObj.optString("userStatus"));
						userBean.setUserName(jsObj.optString("userName"));
						userBean.setGradeId(jsObj.optString("gradeId"));
						userBean.setUserStatus(jsObj.optString("userStatus"));
					} catch (Exception e) {
						e.printStackTrace();
						userBean.setUserName(unkonwLoginArray[0]);
                		userBean.setGradeId("2");
                		userBean.setUserStatus(unkonwLoginArray[3]);
						//isAIDLSuccess = false;
					}
				}else{
					userBean.setUserName(unkonwLoginArray[0]);
            		userBean.setGradeId("2");
            		userBean.setUserStatus(unkonwLoginArray[3]);
				}

				if (mIMerchService != null) {
					try {
						String merchName = mIMerchService.getMerchInfo()
								.getMerchName();
						if(merchName.equals("null"))
							merchName = unkonwLoginArray[1];
						String merchId = mIMerchService.getMerchInfo()
								.getMerchId();
						if(merchId == null)
							merchId = "";
						String terminalId = mIMerchService.getMerchInfo()
								.getTerminalId();
						if(terminalId == null)
							terminalId = "";
						MyLog.e("merchName:" + merchName + " merchId:"
								+ merchId + " terminalId:" + terminalId);

						userBean.setMerchName(merchName);
						userBean.setMerchId(merchId);
						userBean.setTerminalId(terminalId);
						
						if (isAIDLSuccess) {
							// success
							MyLog.i("------has login pay----");
							if(userBean.getUserName() == null ||userBean.getUserName().trim().length() == 0){
								userBean.setMerchName(unkonwLoginArray[1]);
								userBean.setUserName(unkonwLoginArray[0]);
							}
							myApp.setUserBean(userBean);
							refreshUserInfor();
						} else {
							showLoginErrorDialog();
						}
					} catch (Exception e) {
						e.printStackTrace();
						MyLog.i("------not login pay----");
						userBean.setMerchName(unkonwLoginArray[1]);
	            		userBean.setMerchId("");
	            		userBean.setTerminalId("");
	            		myApp.setUserBean(userBean);
	            		refreshUserInfor();
						//showLoginErrorDialog();
					}
				}else{
					MyLog.i("------not install pay----");
					userBean.setMerchName(unkonwLoginArray[1]);
            		userBean.setMerchId("");
            		userBean.setTerminalId("");
            		myApp.setUserBean(userBean);
            		refreshUserInfor();
				}
			}
		}, 500);
	}
}
