package cn.koolcloud.ipos.appstore;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.koolcloud.ipos.appstore.fragment.AppDetailCommentFragment;
import cn.koolcloud.ipos.appstore.fragment.AppDetailCommentFragment.OnFragmentActionListener;
import cn.koolcloud.ipos.appstore.fragment.AppDetailLeftFragment;
import cn.koolcloud.ipos.appstore.fragment.AppDetailLeftFragment.OnSoftwareDetailLeftAttachedListener;
import cn.koolcloud.ipos.appstore.fragment.AppDetailMainFragment;
import cn.koolcloud.ipos.appstore.fragment.AppDetailMainFragment.OnSoftwareDetailAttachedListener;
import cn.koolcloud.ipos.appstore.ui.UserBean;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.pos.service.IMerchService;
import cn.koolcloud.pos.service.ISecureService;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class AppDetailActivity extends BaseActivity implements SearchView.OnQueryTextListener,
		SearchView.OnSuggestionListener, ActionBar.TabListener, OnSoftwareDetailLeftAttachedListener,
		OnSoftwareDetailAttachedListener, OnFragmentActionListener {
	private ActionBar actionBar;											//action bar
	private boolean useLogo = false;										//action bar logo
	private LinearLayout waitingViewContainerLayout;						//contain all the details of waiting view
	private RelativeLayout noNetWorkContainerLayout;						//contain all the details of no network
	private ViewStub viewStub;
	private static FragmentManager fragmentManager = null;
	private Bundle mBundle;
	private static AppDetailActivity instance;
	protected IMerchService mIMerchService;
	private String[] unkonwLoginArray = null;
	private MyApp myApp;
	
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

	
	public static AppDetailActivity getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		
		setContentView(R.layout.software_detail);
		fragmentManager = getSupportFragmentManager();
		mBundle = getIntent().getExtras();

		myApp = (MyApp) getApplication();
		unkonwLoginArray = getResources().getStringArray(R.array.unknow_login_array);

		activityList.add(this);
		initActionBar();
		
		initFragments();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		boundServices();
		MyLog.i("-----onStart  categoryActivity------");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				UserBean userBean = myApp.getUserBean();
				if (mISecureService != null) {
					try {
						String userInfo = mISecureService.getUserInfo();
						if(userInfo != null){
							JSONObject jsObj = new JSONObject(userInfo);
							MyLog.e("operator:" + jsObj.optString("userName") + " gradeId:" + jsObj.optString("gradeId") + " userStatus:" + jsObj.optString("userStatus"));
	                		MyLog.i("-----onStart  categoryActivity------");
	                		if(userBean != null){
	                			userBean.setUserName(jsObj.optString("userName"));
	                    		userBean.setGradeId(jsObj.optString("gradeId"));
	                    		userBean.setUserStatus(jsObj.optString("userStatus"));
	                    		
	                		}
						}else{
							userBean.setUserName(unkonwLoginArray[0]);
	                		userBean.setGradeId("2");
	                		userBean.setUserStatus(unkonwLoginArray[3]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						userBean.setUserName(unkonwLoginArray[0]);
                		userBean.setGradeId("2");
                		userBean.setUserStatus(unkonwLoginArray[3]);
						MyLog.i("-----onStart Exception  categoryActivity------");
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
                		
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					//myApp.setUserBean(userBean);
				}
			}
		}, 500);
	}
	
	@Override
	protected void onStop() {
		unboundServices();
		super.onStop();
	}
	
	private void initFragments() {
		viewStub = (ViewStub) findViewById(R.id.viewstub);
		viewStub.setVisibility(View.VISIBLE);
		waitingViewContainerLayout = (LinearLayout) findViewById(R.id.software_loading);
		waitingViewContainerLayout.setVisibility(View.GONE);
		noNetWorkContainerLayout = (RelativeLayout) findViewById(R.id.nonetwork);
		noNetWorkContainerLayout.setVisibility(View.GONE);
	}
	
	/**
	* @Title: initActionBar
	* @Description: Initialize Action Bar
	* @param 
	* @return void 
	* @throws
	*/
	private void initActionBar() {
		
		actionBar = getSupportActionBar();

        // set defaults for logo & home up
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(useLogo);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bg));
        actionBar.setIcon(R.drawable.ic_launcher_back);
        actionBar.setTitle(" "+getResources().getString(R.string.app_detail));
        showTabsNav();
		// set up list nav
        actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(this, R.array.sections,
                        R.layout.sherlock_spinner_dropdown_item),
                new OnNavigationListener() {
                    public boolean onNavigationItemSelected(int itemPosition,
                            long itemId) {
                        // FIXME add proper implementation
                        return false;
                    }
                });
	}
	
	private void showTabsNav() {
        if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
        	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }
	
	/*@Override
	protected void onDestroy() {
		activityList.remove(this);
		super.onDestroy();
	}*/

	public void refreshLocalSoftData() {
		((MyApp) application).initApps();
		try {
			Thread.currentThread();
			//delay execute invoke refresh data set
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		AppDetailLeftFragment.refreshDataStatus();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	//implement these two interfaces to pass argument after the fragments created on the host activity
	//otherwise fragmentManager will find a null fragment.
	@Override
	public void onDetailLeftActivityCreated() {
		AppDetailLeftFragment detailLeftFragment = (AppDetailLeftFragment)
				fragmentManager.findFragmentById(R.id.software_detail_left);
		detailLeftFragment.setArguments(mBundle);
	}

	@Override
	public void onDetailActivityCreated() {
		AppDetailMainFragment detailLeftFragment = (AppDetailMainFragment)
				fragmentManager.findFragmentById(R.id.software_detail_main);
		detailLeftFragment.setArguments(mBundle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppDetailLeftFragment.SOFTWARE_DETAIL_LEFT_REQUEST || requestCode ==  AppDetailLeftFragment.HANDLE_CHECK_PAYMENT) {
			AppDetailLeftFragment detailLeftFragment = (AppDetailLeftFragment)
					fragmentManager.findFragmentById(R.id.software_detail_left);
			if (detailLeftFragment != null) {
				detailLeftFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		if (requestCode == AppDetailMainFragment.SOFTWARE_DETAIL_MAIN_COMMENT_REQUEST) {
			AppDetailMainFragment detailFragment = (AppDetailMainFragment)
					fragmentManager.findFragmentById(R.id.software_detail_main);
			if (detailFragment != null) {
				detailFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		
	}

	@Override
	public void closeCommentFragment() {
		AppDetailCommentFragment commentFragment = (AppDetailCommentFragment)
				fragmentManager.findFragmentById(R.id.software_detail_main);
		if (commentFragment != null) {
			fragmentManager.beginTransaction().remove(commentFragment).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
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
			Toast.makeText(getApplication(), "绑定失败", 5000).show();
		}
	}

	private void unboundServices() {
		if(merchConnection != null)
			unbindService(merchConnection);
		if(secureConnection != null)
			unbindService(secureConnection);
	}

}
