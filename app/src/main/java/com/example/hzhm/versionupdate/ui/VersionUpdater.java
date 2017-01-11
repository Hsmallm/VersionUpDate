package com.example.hzhm.versionupdate.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hzhm.versionupdate.BuildConfig;
import com.example.hzhm.versionupdate.api.VersionUpdateApi;
import com.example.hzhm.versionupdate.model.VersionUpdateModel;
import com.example.hzhm.versionupdate.utils.ApkDownloadUtil;
import com.example.hzhm.versionupdate.utils.ThApplication;
import com.example.hzhm.versionupdate.utils.ToastUtil;
import com.example.hzhm.versionupdate.utils.serves.RestCallback;
import com.example.hzhm.versionupdate.utils.serves.ServerResultCode;

import java.io.File;

/**
 * Created by hzhm on 2016/11/17.
 * <p>
 * 功能描述：版本更新工具类...
 */
public class VersionUpdater {
    private static final int NOTIFICATION_IS_ON = 0x1;
    private Activity activity;
    private int lastProgress;
    private String filePath;
    private DialogUpdate updateDialog;
    private String downLoadUrl;//下载链接...

    /**
     * @param activity
     * @param viewInSettingPage 设置页面自动检查最新版本
     * @param afterClick        设置页面点击查询最新版本
     */
    public void checkNewVersion(final Activity activity, final TextView viewInSettingPage, final boolean afterClick) {
//        if (null != updateDialog && updateDialog.isShowing()) {//已经显示了
//            return;
//        }
        this.activity = activity;
        try {
            downLoadUrl = "http://172.30.250.111/operation/apk/download?channelCode=360promo";
            //获取相关的渠道信息...
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
//            String msg = info.metaData.getString("myMsg");
//            downLoadUrl = downLoadUrl + msg;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        VersionUpdateApi.init();
        VersionUpdateApi.getVersionUpdater(new RestCallback<VersionUpdateModel>() {
            @Override
            public void onSuccess(VersionUpdateModel model) {
                try {
                    if (model == null || model.result == null) {
                        return;
                    }
                    String version = model.result.currentVersion;
                    String minVersion = model.result.allowLowestVersion;
                    String currentV = BuildConfig.VERSION_NAME;
                    Log.e("errorCode", "version:" + version + " minVersion:" + minVersion + " currentVersion:" + currentV);
                    //---情况一：判断传入的当前Activity是否含有提示文本控件...
                    if (viewInSettingPage != null) {
                        if (!model.result.forceUpdate.isEmpty()) {
                            viewInSettingPage.setText("有新版本");
                            if (null != updateDialog && updateDialog.isShowing()) {
                                if (model.result.forceUpdate.equals("0")) {//0、强制更新
                                    beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, true);
                                } else if (model.result.forceUpdate.equals("1")) {//1、一般更新
                                    if (currentV.compareTo(minVersion) < 0) {//强制更新
                                        beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, true);
                                    } else {//一般更新
                                        beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, false);
                                    }
                                } else if (model.result.forceUpdate.equals("2")) {//2、-静默更新
                                    quietDownLoad(downLoadUrl, model.result.currentVersion);
                                }
                            }
                        } else {//无需更新
                            viewInSettingPage.setText("V" + BuildConfig.VERSION_NAME);
                        }
                        return;
                    }

                    //---情况二：如果传入的当前Activity沒有传入文本控件...
                    if (model.result.forceUpdate.equals("0")) {//0、强制更新
                        //低于最低版本号，强制更新
                        beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, true);
                    } else if (model.result.forceUpdate.equals("1")) {//1、一般更新
                        if (currentV.compareTo(minVersion) < 0) {//强制更新
                            beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, true);
                        } else {//一般更新
                            if (afterClick) {
                                beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, false);
                            } else {
                                //介于最低版本和服务器最高版本号之间，提示更新
                                beforeDownLoad(downLoadUrl, model.result.description, model.result.currentVersion, false);
                            }
                        }
                    } else if (model.result.forceUpdate.equals("2")) {//2、-静默更新
                        quietDownLoad(downLoadUrl, model.result.currentVersion);
                    } else {
                        //无需更新
                        Log.i("errorCode", " 无需更新");
                        if (afterClick) {
                            ToastUtil.showNormalToast("当前已是最新版本");
                        }
                    }
                } catch (Exception e) {
                    Log.e("e",e+"");
                }
            }

            @Override
            public void onFail(ServerResultCode serverResultCode, String errorMessage) {
                ToastUtil.showNormalToast("onFail");
            }
        });
    }

    private void beforeDownLoad(final String downLoadUrl, String description, final String version, final boolean isForcingUpdating) {
        //如果正在下载中就不执行下载信息的代码
        if (0 < lastProgress && lastProgress < 100) {
            return;
        }

        if (null == updateDialog)
            updateDialog = new DialogUpdate(activity);

        //新本的apk已经下载完成，点击安装
        if (ApkDownloadUtil.checkNewVersionApkIsExist(filePath,version)) {
            ToastUtil.showNormalToast("您已下载过该版本，立即安装即可");
            updateDialog.onResumeWhileDownloadSuccess(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //安装新版apk
                    ApkDownloadUtil.installApk(activity, Uri.fromFile(new File(filePath)));
                }
            });
            return;
        }

        // 设置下载版本的相关信息
        updateDialog.setInfo(isForcingUpdating, description, version,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ApkDownloadUtil.existSDCard()) {
                            ToastUtil.showNormalToast("存储卡没有正确安装,请先确认安装");
                            return;
                        }
                        updateDialog.startUpdate();
                        downLoad(downLoadUrl, updateDialog);
                    }
                }, isForcingUpdating ? null : new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (0 < lastProgress && lastProgress < 100) {
                            //取消下载
                            ToastUtil.showNormalToast("亲，正在下载中，请耐心等待哦..");
                        } else {
                            updateDialog.dismiss();
                        }
                    }
                });

        if (!updateDialog.isShowing()) {
            updateDialog.show();
        }
    }

    /**
     * 静默下载...
     *
     * @param downLoadUrl
     */
    private void quietDownLoad(String downLoadUrl, String version) {
        if (ApkDownloadUtil.checkNewVersionApkIsExist(filePath,version)) {//新本的apk已经下载完成，点击安装
            ToastUtil.showNormalToast("您已下载过该版本，立即安装即可");
            updateDialog.onResumeWhileDownloadSuccess(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApkDownloadUtil.installApk(activity, Uri.fromFile(new File(filePath)));
                }
            });
            return;
        }
        filePath = ThApplication.getInstance().getExternalCacheDir() + "/TRC-" + BuildConfig.VERSION_NAME + "-" + System.currentTimeMillis() + ".apk";
        ApkDownloadUtil.downloadApk(Uri.parse(downLoadUrl), Uri.fromFile(new File(filePath)), new ApkDownloadUtil.DownloadListener() {
            @Override
            public void onDownloadComplete(int id, Uri downloadUri) {
                ToastUtil.showNormalToast("下载完成,正在准备安装新版本...");
                ApkDownloadUtil.installApk(activity, downloadUri);
            }

            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                ToastUtil.showNormalToast("下载失败,点击重试");
            }

            @Override
            public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

            }
        });
    }

    /**
     * 下载
     * @param downLoadUrl
     * @param dialog
     */
    private void downLoad(String downLoadUrl, final DialogUpdate dialog) {
        final NotificationManager nm = ApkDownloadUtil.setNotificationStatus(activity,NOTIFICATION_IS_ON);
        filePath = ThApplication.getInstance().getExternalCacheDir() + "/TRC-" + BuildConfig.VERSION_NAME + "-" + System.currentTimeMillis() + ".apk";
        ApkDownloadUtil.downloadApk(Uri.parse(downLoadUrl), Uri.fromFile(new File(filePath)), new ApkDownloadUtil.DownloadListener() {

            //1、下载成功...
            @Override
            public void onDownloadComplete(int id, Uri downloadUri) {
                try {
                    lastProgress = 0;
                    nm.cancel(NOTIFICATION_IS_ON);
                    ToastUtil.showNormalToast("下载完成,正在准备安装新版本...");
                    dialog.whenCompleted();
                    ApkDownloadUtil.installApk(activity, downloadUri);
                } catch (Exception e) {
                    Log.e("e",e+"");
                }
            }

            //2、下载失败...
            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                try {
                    lastProgress = 0;
                    nm.cancel(NOTIFICATION_IS_ON);
                    ToastUtil.showNormalToast("下载失败,点击重试");
                    dialog.whenFailed();
                } catch (Exception e) {
                    Log.e("e",e+"");
                }
            }

            //3、下载过程中...
            @Override
            public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {
                if (lastProgress != progress) {
                    dialog.setProgress(progress);
                    lastProgress = progress;
                    Log.i("errorCode", "progress:" + progress);
                }
            }
        });
    }
}
