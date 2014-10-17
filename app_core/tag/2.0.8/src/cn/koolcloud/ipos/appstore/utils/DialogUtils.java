package cn.koolcloud.ipos.appstore.utils;

import cn.koolcloud.ipos.appstore.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class DialogUtils {

	private static Dialog myDialog = null;
	private static LayoutInflater mInflater;
	private static ImageView imageView = null;
	
	
	public static  void showLoading(Context context) {
		/*Message msg = new Message();
		msg.what = SHOW_LOADING;
		myHandler.sendMessage(msg);*/
		mInflater = LayoutInflater.from(context);
		showProgressDialog(context);
	}
	
	public static void dismissLoading() {
		/*Message msg = new Message();
		msg.what = DISMISS_LOADING;
		myHandler.sendMessage(msg);*/
		dissmissProgressDialog();
	}
	
	private static void showProgressDialog(Context context) {
		dissmissProgressDialog();
		
		myDialog = new Dialog(context,R.style.dialog);
		myDialog.show();
		View view = mInflater.inflate(R.layout.loading_image, null);
		imageView = (ImageView) view.findViewById(R.id.animationImage);
		myDialog.setContentView(view);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
		animationDrawable.start();
	}
	
	private  static void dissmissProgressDialog() {
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
	private static void unbindDrawables(View view) {
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
	
 
}
