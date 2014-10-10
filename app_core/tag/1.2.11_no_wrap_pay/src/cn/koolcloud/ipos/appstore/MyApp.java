package cn.koolcloud.ipos.appstore;


import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import cn.koolcloud.ipos.appstore.cache.DataCleanManager;
import cn.koolcloud.ipos.appstore.ui.UserBean;
import cn.koolcloud.ipos.appstore.utils.Env;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.ipos.appstore.utils.MySPEdit;

import com.baidu.frontia.FrontiaApplication;

/**
 * <p>Title: AppStoreApplication.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2013-11-7
 * @version 	
 */
public class MyApp extends FrontiaApplication {
	public static int THEME = R.style.Theme_Sherlock;
	private LayoutInflater inflater;				//view inflater
	private Map<String, PackageInfo> installedPackage;
	private static final int INIT_APP_INFOS = 3;
	private boolean isFirstStart = false;
	private UserBean userBean;

	@Override
	public void onCreate() {
		super.onCreate();
		String versionNew = Env.getVersionName(this);
		String versionOld = MySPEdit.getUpdateRecordName(this);	
		if(versionOld.compareTo(versionNew) < 0){
			DataCleanManager.cleanApplicationData(this);
			MySPEdit.setUpdateRecordName(this, versionNew);
			MyLog.i("-------start from myApp here after--------" + MySPEdit.getUpdateRecordName(this));
		}
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initApps();
	}
	
	public LayoutInflater getInflater() {
		return inflater;
	}
	
	
	public void saveInstalledAppsInfo(Map<String, PackageInfo> packages) {
		this.installedPackage = packages;
	}
	
	public Map<String, PackageInfo> getInstalledAppsInfo() {
		return this.installedPackage;
	}
	
	public void addAppInfoCache(PackageInfo packageInfo) {
		if (packageInfo != null) {
			
			installedPackage.put(packageInfo.applicationInfo.packageName, packageInfo);
		}
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_APP_INFOS:
				Map<String, PackageInfo> packages = (Map<String, PackageInfo>) msg.obj;
				saveInstalledAppsInfo(packages);
			}
			return false;
		}
	});
	
	public void initApps() {
		new InitAppsThread().start();
	}
	
	public boolean isFirstStart() {
		return this.isFirstStart;
	}
	
	public void setFirstStart(boolean firstStarted) {
		this.isFirstStart = firstStarted;
	}
	
	//init apps in a new thread
	class InitAppsThread extends Thread {
		@Override
		public void run() {
			MyLog.d("invoke scan local apps");
			Long start = System.currentTimeMillis();
			Map<String, PackageInfo> installedPackage = Env.scanInstalledAppToMap(getApplicationContext());
			Long end = System.currentTimeMillis();
			MyLog.d("total time:" + (end - start));
			
			Message msg = mHandler.obtainMessage();
			msg.obj = installedPackage;
			msg.what = INIT_APP_INFOS;
			mHandler.sendMessage(msg);
		}
	}
	
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
}
