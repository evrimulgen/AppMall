package cn.koolcloud.ipos.appstore.download.multithread;

import cn.koolcloud.ipos.appstore.entity.App;


public abstract class Listener {

	public Listener() {
		
	}

	public abstract void handle(Event myEvent, App pkgName, String paramJson, int versionCode);
}
