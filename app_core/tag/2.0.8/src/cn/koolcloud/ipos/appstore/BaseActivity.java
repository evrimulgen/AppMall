package cn.koolcloud.ipos.appstore;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import cn.koolcloud.ipos.appstore.api.ApiService;
import cn.koolcloud.ipos.appstore.constant.Constants;
import cn.koolcloud.ipos.appstore.entity.Client;
import cn.koolcloud.ipos.appstore.entity.ResultSet;
import cn.koolcloud.ipos.appstore.interfaces.CallBack;
import cn.koolcloud.ipos.appstore.utils.Env;
import cn.koolcloud.ipos.appstore.utils.JsonUtils;
import cn.koolcloud.ipos.appstore.utils.MyLog;
import cn.koolcloud.ipos.appstore.utils.MySPEdit;
import cn.koolcloud.ipos.appstore.utils.ToastUtil;
import cn.koolcloud.ipos.appstore.utils.Utils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

/**
 * <p>Title: BaseActivity.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2013-11-26
 * @version 	
 */
public class BaseActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener,
		SearchView.OnSuggestionListener {

	protected MyApp application;// application
	Dialog myDialog = null;
	protected LayoutInflater mInflater;
	private ImageView imageView = null;
	private SearchView searchView;
	public static final int SHOW_LOADING = 1 << 1;
	public static final int DISMISS_LOADING = 1 << 2;
	public static List<Activity> activityList = new LinkedList<Activity>();
	
	private boolean isMenuUpdateClicking = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.d(getClass().getSimpleName() + ".onCreate, Memory allocated size=" +
					getMemoryAllocatedSize() + "M");
		application = (MyApp) getApplication();
		activityList.add(this);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private Handler myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LOADING:
				showProgressDialog();
				break;
			case DISMISS_LOADING:
				dissmissProgressDialog();
				break;
			}
			return false;
		}
	});

	public Handler getHandler() {
		return myHandler;
	}
	
	public void showLoading() {
		showProgressDialog();
	}
	
	public void dismissLoading() {
		dissmissProgressDialog();
	}

	private void showProgressDialog() {
		dissmissProgressDialog();
		
		myDialog = new Dialog(BaseActivity.this, R.style.dialog);
		myDialog.show();
		View view = mInflater.inflate(R.layout.loading_image, null);
		imageView = (ImageView) view.findViewById(R.id.animationImage);
		myDialog.setContentView(view);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
		animationDrawable.start();
	}
	
	private void dissmissProgressDialog() {
		if(myDialog != null) {
			if(myDialog.isShowing()) {
				if(imageView != null) {
					unbindDrawables(imageView);
				}
				myDialog.dismiss();
			}
			myDialog = null;
		}
	}
	
	/**
	* @Title: unbindDrawables
	* @Description: TODO release bitmap resources
	* @param @param view
	* @return void 
	* @throws
	*/
	private void unbindDrawables(View view) {
		Drawable back = view.getBackground();
	    if (back != null) {
	    	back.setCallback(null);
	    }
	    if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	        }
	        ((ViewGroup) view).removeAllViews();
	    }
	}

	@Override
	protected void onDestroy() {
		activityList.remove(this);
		dissmissProgressDialog();
		super.onDestroy();
	}
	
	private double getMemoryAllocatedSize() {
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory()-runtime.freeMemory())/1024.0/1024.0;
	}
	
	public void getClientVersion() {
		ApiService.getUpdateVersion(this, MySPEdit.getTerminalID(getApplicationContext()), 
				Env.getVersionName(getApplicationContext()), getVersionCallBack);
	}
	
	private CallBack getVersionCallBack = new CallBack() {
		@Override
		public void onCancelled() {
			dismissLoading();
		}

		@Override
		public void onStart() {
			showLoading();
		}

		@Override
		public void onSuccess(JSONObject jsonObj) {
			try {
				Client client = null;
				MyLog.d("-------getVersionInfo=" + jsonObj.toString());

				client = JsonUtils.parseJSONClient(jsonObj);
				boolean isFirstStart = application.isFirstStart();
				if (client != null) {
					Intent mIntent = new Intent(getApplicationContext(), UpdateClientDialogActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(Constants.SER_KEY, client);
					mIntent.putExtras(mBundle);
					if (client.getStrategy().equals(Constants.STRATEGY_UPDATE_FORCE)) {  //force update 
						startActivity(mIntent);
					} else { //update option
						if (!isFirstStart) {
							startActivity(mIntent);
							application.setFirstStart(true);
						} else if (isMenuUpdateClicking) {
							startActivity(mIntent);
							isMenuUpdateClicking = false;
						}
					}
				} else { //don't show up version dialog except clicking the menu "check update"
					if (isMenuUpdateClicking) {
						Intent mIntent = new Intent(getApplicationContext(), UpdateClientDialogActivity.class);
						Bundle mBundle = new Bundle();
						mBundle.putSerializable(Constants.SER_KEY, client);
						mIntent.putExtras(mBundle);
						startActivity(mIntent);
						isMenuUpdateClicking = false;
					}
				}
				dismissLoading();
			} catch (Exception e) {
				onFailure("app version response error!");
			}
		}

		@Override
		public void onFailure(String msg) {
			dismissLoading();
			MyLog.d("describe=" + msg);
			if (msg.contains(ResultSet.NET_ERROR.describe)) {
				ToastUtil.showToast(getApplicationContext(), R.string.nonetwork_prompt_server_error);
			}
		}
	};
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		// set up a listener for the refresh item
//        final MenuItem aboutItem = (MenuItem) menu.findItem(R.id.menu_setting_abount);
//        aboutItem.setOnMenuItemClickListener(new MenuItemClickListener());
//        MenuItem checkUpdateClient = (MenuItem) menu.findItem(R.id.menu_setting_check_update);
//        checkUpdateClient.setOnMenuItemClickListener(new MenuItemClickListener());
//        MenuItem settingClient = (MenuItem) menu.findItem(R.id.menu_setting_settings);
//        settingClient.setOnMenuItemClickListener(new MenuItemClickListener());
//        MenuItem menuExit = (MenuItem) menu.findItem(R.id.menu_setting_exit);
//        menuExit.setOnMenuItemClickListener(new MenuItemClickListener());
        
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint(Utils.getResourceString(application, R.string.search_message_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        
        return true;
    }
	
	//menu item click listener on action bar
//	class MenuItemClickListener implements OnMenuItemClickListener {
//		@Override
//		public boolean onMenuItemClick(MenuItem item) {
//			switch (item.getItemId()) {
//			case R.id.menu_setting_abount:
//				startActivity(new Intent(BaseActivity.this, AboutDialogActivity.class));
//				break;
//			case R.id.menu_setting_check_update:
//				isMenuUpdateClicking = true;
//				getClientVersion();
//				break;
//			case R.id.menu_setting_settings:
//				startActivity(new Intent(BaseActivity.this, SettingActivity.class));
//				break;
//			case R.id.menu_setting_exit:
//				exit();
//				break;
//			}
//			return false;
//		}
//	}
	
	
	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(searchView != null){
			searchView.onActionViewCollapsed();
		}
	}

	@Override
	public boolean onSuggestionClick(int position) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Intent intent = new Intent(application, SearchActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(Constants.SEARCH_WORD_KEY, query);
		intent.putExtras(bundle);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	        finish();
	        break;
	    }
		return true;
	}
	
	protected void exit() {
		if (activityList != null && activityList.size() > 0) {
			for (int i = 0; i < activityList.size(); i++) {
				Activity activity = activityList.get(i);
				if (activity != null) {
					activity.finish();
				}
			}
			application.setFirstStart(false);
		}
	}
}
