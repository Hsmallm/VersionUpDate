package com.example.hzhm.versionupdate.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.hzhm.versionupdate.utils.downloadmanager.DefaultRetryPolicy;
import com.example.hzhm.versionupdate.utils.downloadmanager.DownloadRequest;
import com.example.hzhm.versionupdate.utils.downloadmanager.DownloadStatusListener;
import com.example.hzhm.versionupdate.utils.downloadmanager.ThinDownloadManager;

/**
 * Created by hzhm on 2017/1/6.
 *
 * 功能描述：App下载及其安装的工具类...
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
                            Log.e("e",e+"");
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
            Log.e("e",e+"");
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

    public interface DownloadListener {
        void onDownloadComplete(int id, Uri downloadUri);

        void onDownloadFailed(int id, int errorCode, String errorMessage);

        void onProgress(int id, long totalBytes, long downloadedBytes, int progress);
    }
}
