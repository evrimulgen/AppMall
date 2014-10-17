package cn.koolcloud.ipos.appstore.utils;


import cn.koolcloud.ipos.appstore.R;
import android.content.Context;
import android.provider.Settings;

/**
 * <p>Title: Utils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2013-11-1
 * @version 	
 */
public class Utils {
	
	public static String getString(String str, String defaultString) {
		return str == null ? defaultString : str;
	}

	public static int getInt(String str, int defaultInt) {
		if (str == null) {
			return defaultInt;
		}
		try {
			defaultInt = Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}

		return defaultInt;
	}

	public static float getFloat(String str, float defaultInt) {
		if (str == null) {
			return defaultInt;
		}
		try {
			defaultInt = Float.parseFloat(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return defaultInt;
	}

	public static String getResourceString(Context context, int strId) {
		return context.getResources().getString(strId);
	}

	public static Class<?> stringToClass(String classname){
		Class<?> newClass = null;
		try {
			newClass =  Class.forName(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return newClass;
	}
	
	/**
	 * @Title: bubbleSort
	 * @Description: Bubble sort
	 * @param args
	 * @return
	 * @return: int[]
	 */
	public static int[] bubbleSort(int[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[i] < args[j]) {
                        int temp = args[i];
                        args[i] = args[j];
                        args[j] = temp;
                }
            }
        }
        return args;
	}
	
	public static void judgeTimeIsError(final Context context){
		 MyLog.i("Categoryright----judgeTimeIsError---:");
		 final int ON = 1;  
			final int OFF = 0;  
			int nAutoTimeStatus = Settings.System.getInt(  
	               context.getContentResolver(), Settings.System.AUTO_TIME, OFF); 
			 MyLog.i("当前时间同步状态：" + nAutoTimeStatus);  
			 if(nAutoTimeStatus == OFF){
				 Settings.System.putInt(context.getContentResolver(),  
		                 Settings.System.AUTO_TIME, nAutoTimeStatus == OFF ? ON  
		                         : ON); 
				 MySPEdit.setTimeIsError(context, true);
				 MyLog.i("now--------------"+MySPEdit.getTimeIsError(context));
				 ToastUtil.showToast(context, R.string.time_sync, true);
			 }else{
				 MySPEdit.setTimeIsError(context, false);
			 }
		}
}
