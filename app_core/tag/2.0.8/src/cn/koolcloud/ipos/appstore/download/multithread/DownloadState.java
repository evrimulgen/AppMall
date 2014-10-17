package cn.koolcloud.ipos.appstore.download.multithread;

import cn.koolcloud.ipos.appstore.entity.App;

public abstract class DownloadState {
	
	public abstract void Handle(DownloadContext context, App pkgname, String paramJson, int versionCode);

}
