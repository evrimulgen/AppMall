package cn.koolcloud.ipos.appstore.download.multithread;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import cn.koolcloud.ipos.appstore.constant.Constants;
import cn.koolcloud.ipos.appstore.entity.App;
import cn.koolcloud.ipos.appstore.utils.MyLog;

public class MultiThreadService extends Service{

	/* 缓存各包下载的FileDownloader对象和状态*/
	private Map<String, FileDownloader> loaderMap = null;
	private IBinderImple binder = new IBinderImple();
	private StateRecord stateRecord;
	
	public static final int IS_DOWNLOADING = 1;
	public static final int IS_PAUSEING = 2;
	public static final int HAVE_FINISHED = 3;

    public class IBinderImple extends Binder {

    	/**
      	 * 主线程(UI线程)
      	 * 对于显示控件的界面更新只是由UI线程负责，如果是在非UI线程更新控件的属性值，更新后的显示界面不会反映到屏幕上
      	 * 如果想让更新后的显示界面反映到屏幕上，需要用Handler设置。
      	 * @param path
      	 * @param savedir
      	 */
    	public void StartDownload(final String downloadUrl, final File savedir,
    			final int threadnum, final App app, final String paramJson,
    			final int versionCode) {

        	new Thread(new Runnable() {
    			@Override
    			public void run() {
    				//下载前初始化工作
    				//存  对外状态数据库  和 任务Map都用name+version
    				FileDownloader loader = loaderMap.get(app.getPackageName()+Integer.toString(versionCode));
    				if(loader == null) {
    					loader = new FileDownloader(MultiThreadService.this, downloadUrl,
    						savedir, threadnum, paramJson, app.getPackageName() + ".apk");
    					loaderMap.put(app.getPackageName()+Integer.toString(versionCode), loader);
    				}
    				loader.start();
    				stateRecord.delete(app.getPackageName()+Integer.toString(versionCode));
    				stateRecord.insert(app.getPackageName()+Integer.toString(versionCode), IS_DOWNLOADING, 0);

        			Intent intent = new Intent(Constants.ACTION_TASK_STARTED);
        	        intent.putExtra(Constants.TASK_PACKAGE_NAME, app.getPackageName());
        	        MultiThreadService.this.sendBroadcast(intent);
    		    	final int filesize = loader.getFileSize();//设置进度条的最大刻度为文件的长度

    				try {
    					//开始下载
    					loader.download(new DownloadProgressListener() {
    						@Override
    						public void onDownloadSize(long size, boolean notfinish) {//实时获知文件已经下载的数据长度
    							double sized = (double)size*100;
    							double filesized = (double)filesize;
    							double percentd = sized/filesized;
    							int percent = (int)percentd;
    							if(percent > 100)
    								percent = 100;
    							stateRecord.updatePercent(app.getPackageName()+Integer.toString(versionCode), percent);
    							Intent intent = new Intent(Constants.ACTION_TASK_UPDATED);
    					        intent.putExtra(Constants.TASK_COMPLETE_PROCESS, percent);
    					        intent.putExtra(Constants.TASK_PACKAGE_NAME, app.getPackageName());
    					        MultiThreadService.this.sendBroadcast(intent);
    					        if(!notfinish){
    					        	File f = new File(MultiThreadService.this.getFilesDir()+"/"+app.getPackageName() + ".apk");
    					        	try {
    					        		FileInputStream inputStream = new FileInputStream(f);
										int length = inputStream.available();
										inputStream.close();
										if(length != Integer.parseInt(app.getSize())){
											if(f.exists())
												f.delete();
										}
										MyLog.i("------------"+length +"============="+app.getSize());
		    					        FinishDownload(app.getPackageName(), versionCode);
									} catch (Exception e) {
										e.printStackTrace();
									}
    					        	
    					        }
    						}
    					});
    				} catch (Exception e) {
    					e.printStackTrace();
    					Intent intent1 = new Intent(Constants.ACTION_TASK_ERROR);
    			        intent1.putExtra(Constants.TASK_PACKAGE_NAME, app.getPackageName());
    			        MultiThreadService.this.sendBroadcast(intent1);
    				}
    			}
    		}).start();
    	}

    	public void PauseDownload(String packageName, int versionCode) {
    		FileDownloader loader = loaderMap.get(packageName+Integer.toString(versionCode));
    		if(loader != null)
    		{
    			loader.pause();
    			stateRecord.updateState(packageName+Integer.toString(versionCode), IS_PAUSEING);
    			Intent intent = new Intent(Constants.ACTION_TASK_PAUSED);
    	        intent.putExtra(Constants.TASK_PACKAGE_NAME, packageName);
    	        MultiThreadService.this.sendBroadcast(intent);
    		}
    	}

    	public void CancelDownload(String packageName, int versionCode) {
    		FileDownloader loader = loaderMap.get(packageName+Integer.toString(versionCode));
    		if(loader != null)
    		{
    			loader.cancel();
    			stateRecord.delete(packageName+Integer.toString(versionCode));
    			Intent intent = new Intent(Constants.ACTION_TASK_CANCEL);
    	        intent.putExtra(Constants.TASK_PACKAGE_NAME, packageName);
    	        MultiThreadService.this.sendBroadcast(intent);
    		}
    	}

    	public void FinishDownload(String packageName, int versionCode) {
    		FileDownloader loader = loaderMap.get(packageName+Integer.toString(versionCode));
    		if(loader != null)
    		{
    			stateRecord.updateState(packageName+Integer.toString(versionCode), HAVE_FINISHED);
    			Intent intent = new Intent(Constants.ACTION_TASK_FINISHED);
    			intent.putExtra(Constants.TASK_PACKAGE_NAME, packageName);
    			MultiThreadService.this.sendBroadcast(intent);
        		loaderMap.remove(packageName+Integer.toString(versionCode));
    		}
    	}
    	
    	public HashMap<Integer, Integer> GetStateData(String packageName, int versionCode) {
    		File f = new File(getFilesDir(), packageName+".apk");
    		if(!f.exists()) {
    			stateRecord.delete(packageName+Integer.toString(versionCode));
    			return null;
    		}
    		return stateRecord.getData(packageName+Integer.toString(versionCode));
    	}
    	
    	public void CancelAllTask() {
    		for(Entry<String, FileDownloader> entry : loaderMap.entrySet()) {
    			 FileDownloader loader = entry.getValue();
    			 loader.pause();
    		}
    		loaderMap.clear();
    		stateRecord.clear();
    		DownloadRecord dr = DownloadRecord.getInstance();
    		if(dr != null)
    			dr.clear();
    	}

    }
    
	@Override
	public void onCreate() {
		super.onCreate();
		stateRecord = StateRecord.getInstance(this);
		stateRecord.initState();
		if(loaderMap == null)
			loaderMap = new ConcurrentHashMap<String, FileDownloader>();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(loaderMap.size() > 0){//如果存在任务记录
			for(Entry<String, FileDownloader> entry : loaderMap.entrySet()) {
			    FileDownloader loader = entry.getValue();
			    loader.pause();
			}
			loaderMap.clear();
		}
		stateRecord.close();
		DownloadRecord.getInstance(this).close();
	}

	@Override
	public IBinder onBind(Intent intent) {//连续绑定只执行一次
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
