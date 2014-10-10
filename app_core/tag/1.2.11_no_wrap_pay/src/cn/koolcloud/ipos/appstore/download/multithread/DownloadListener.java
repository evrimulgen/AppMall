package cn.koolcloud.ipos.appstore.download.multithread;

import cn.koolcloud.ipos.appstore.MainActivity;
import cn.koolcloud.ipos.appstore.api.ApiService;
import cn.koolcloud.ipos.appstore.entity.App;
import cn.koolcloud.ipos.appstore.utils.MySPEdit;

public class DownloadListener extends Listener {

	public DownloadListener() {
		super();
	}

	@Override
	public void handle(Event myEvent, App app, String paramJson, int versionCode) {
		
		MainActivity ctx = MainActivity.getInstance();
		MultiThreadService.IBinderImple binder = ctx.getBinder();
		
		if(myEvent.getEvent().equals(Event.startEvent))
		{
			binder.StartDownload(ApiService.getDownloadAppUrl(), ctx.getFilesDir(),
					MySPEdit.getMultiThreadNums(ctx), app, paramJson, versionCode);
		}
		else if(myEvent.getEvent().equals(Event.pauseEvent))
		{
			binder.PauseDownload(app.getPackageName(), versionCode);
		}
		else if(myEvent.getEvent().equals(Event.cancelEvent))
		{//预留接口，功能已实现，暂无使用
			binder.CancelDownload(app.getPackageName(), versionCode);
		}
		else if(myEvent.getEvent().equals(Event.finishEvent))
		{//正常下载完成后，StartDownload里面包含善后工作。
		 //此处为下载完成后默认转到安装功能，不再放入MultiThreadUtil模块
		}
	}

}
