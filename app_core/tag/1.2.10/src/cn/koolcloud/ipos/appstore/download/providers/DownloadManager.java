package cn.koolcloud.ipos.appstore.download.providers;

import android.content.Context;

import cn.koolcloud.ipos.appstore.download.common.DownloadConstants;
import cn.koolcloud.ipos.appstore.download.common.DownloadVariable;

public class DownloadManager {
    private static DownloadManager dm;

    public static DownloadManager getInstance(Context context) {
        if (dm == null) {
            dm = new DownloadManager(context);
        }
        return dm;
    }

    public DownloadManager(Context context) {
        DownloadVariable.g_Context = context;
    }

    /**
     * ֧�����������Ϊ3������
     * 
     * @param count
     */
    public void setMaxTaskCount(int count) {
        if (count > 3) {
            DownloadVariable.MAX_TASK_COUNT = 3;
        } else if (count < 0) {
            DownloadVariable.MAX_TASK_COUNT = 0;
        } else {
            DownloadVariable.MAX_TASK_COUNT = count;
        }
    }

    /**
     * ��������״̬��Ĭ�����κ����綼�������ء�0���κ����綼�������أ�1����ʹ��wifi��������
     */
    public void setDownloadNetwork(int networkType) {
        switch (networkType) {
        case DownloadConstants.DOWNLOAD_NETWORK_ALL:
            DownloadVariable.SUPPORT_NETWORK_TYPE = DownloadConstants.DOWNLOAD_NETWORK_ALL;
            break;
        case DownloadConstants.DOWNLOAD_NETWORK_ONLYWIFI:
            DownloadVariable.SUPPORT_NETWORK_TYPE = DownloadConstants.DOWNLOAD_NETWORK_ONLYWIFI;
            break;
        default:
            DownloadVariable.SUPPORT_NETWORK_TYPE = DownloadConstants.DOWNLOAD_NETWORK_ALL;
        }
    }

    /**
     * ��������
     */
    public void addDownloadTask() {

    }

    /**
     * ɾ������
     */
    public void deleteDownloadTask() {

    }

    /**
     * ��ͣ����
     */
    public void pauseDownloadTask() {

    }

}
