package cn.koolcloud.ipos.appstore.download.multithread;

public interface DownloadProgressListener {
	public void onDownloadSize(long size, boolean notfinish);
}
