package com.example.hzhm.versionupdate.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.hzhm.versionupdate.R;
import com.example.hzhm.versionupdate.utils.downloadmanager.DefaultRetryPolicy;
import com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequest;
import com.example.hzhm.versionupdate.utils.downloadmanager.DownloadStatusListener;
import com.example.hzhm.versionupdate.utils.downloadmanager.ThinDownloadManager;

import java.io.File;

/**
 * Created by hzhm on 2017/1/6.
 * <p>
 * 功能描述：App下载及其安装的工具类（注：里面还集成又Apk安装包的检测、SD检测、下载时通知栏设置）...
 */

public class ApkDownloadUtil {

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 2;
    //实例化下载管理类...
    private static ThinDownloadManager downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
    private static int lastProgress = 0;

    /**
     * 下载APP
     *
     * @param downloadUri
     * @param destinationUri
     * @param downloadListener
     */
    public static void downloadApk(Uri downloadUri, final Uri destinationUri, final DownloadListener downloadListener) {
        //实例化一个下载请求对象...
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        downloadListener.onDownloadComplete(id, destinationUri);
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        try {
                            downloadListener.onDownloadFailed(id, errorCode, errorMessage);
                            //当下载失败时，取消所有下载...
                            downloadManager.cancelAll();
                        } catch (Exception e) {
                            Log.e("e", e + "");
                        }
                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {
                        if (lastProgress != progress) {
                            lastProgress = progress;
                            downloadListener.onProgress(id, totalBytes, downloadedBytes, progress);
                        }
                    }
                });
        //往下载管理类中添加下载请求...
        downloadManager.add(downloadRequest);
    }

    public static void cancleDownload() {
        try {
            downloadManager.cancelAll();
        } catch (Exception e) {
            Log.e("e", e + "");
        }
    }

    /**
     * 跳转到安装APP界面...
     *
     * @param activity
     * @param destinationUri
     */
    public static void installApk(Activity activity, Uri destinationUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(destinationUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 下载请求回调接口
     */
    public interface DownloadListener {
        void onDownloadComplete(int id, Uri downloadUri);

        void onDownloadFailed(int id, int errorCode, String errorMessage);

        void onProgress(int id, long totalBytes, long downloadedBytes, int progress);
    }


    /**
     * 检查最新版本的apk是否已经安装...
     *
     * @return 新版本apk已经下载完成true
     */
    public static boolean checkNewVersionApkIsExist(String filePath, String version) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                if (new File(filePath).exists()) {
                    String[] arr = filePath.split("-");
                    if (!version.endsWith(arr[1])) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("e", e + "");
        }
        return false;
    }

    /**
     * 下载时设置通知相应的显示状态...
     *
     * @return
     */
    public static NotificationManager setNotificationStatus(Activity activity, int NOTIFICATION_IS_ON) {
        final NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText("正在下载更新...")
                .setAutoCancel(false)
                .setOngoing(true);
        nm.notify(NOTIFICATION_IS_ON, notificationBuilder.build());
        return nm;
    }

    /**
     * 检查SD卡是否存在...
     *
     * @return
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
