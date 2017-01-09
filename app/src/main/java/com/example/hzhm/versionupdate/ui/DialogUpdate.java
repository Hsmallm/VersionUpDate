package com.example.hzhm.versionupdate.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hzhm.versionupdate.R;

/**
 * Created by hzhm on 2016/11/16.
 *
 * 功能描述：版本更新dialog
 */
public class DialogUpdate extends Dialog {
    private View imgClose;
    private TextView txtVersion;
    private TextView txtContent;
    private TextView txtUpdate;
    private View viewProgress;
    private ProgressBar progressbBar;
    private TextView txtProgress;

    public DialogUpdate(Context context) {
        this(context, 0);
    }

    public DialogUpdate(Context context, int themeResId) {
        super(context, R.style.DialogTransletTheme);
        setCancelable(false);
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_for_update, null);
        setContentView(dialog);
        imgClose = dialog.findViewById(R.id.close);
        txtVersion = (TextView) dialog.findViewById(R.id.updateVersion);
        txtContent = (TextView) dialog.findViewById(R.id.updateContent);
        txtUpdate = (TextView) dialog.findViewById(R.id.updateBtn);
        viewProgress = dialog.findViewById(R.id.updatePgressView);
        progressbBar = (ProgressBar) dialog.findViewById(R.id.updateProgess);
        txtProgress = (TextView) dialog.findViewById(R.id.updateTxtProgress);
    }

    /**
     * 设置下载版本的相关信息
     *
     * @param isForcingUpdate 是否强制更新
     * @param strContent 详情描述
     * @param versionName 当前版本
     * @param updateBtnListener 点击下载监听
     * @param cancleListener 点击取消监听
     */
    public void setInfo(boolean isForcingUpdate, String strContent, String versionName, View.OnClickListener updateBtnListener, View.OnClickListener cancleListener) {
        if (isForcingUpdate)
            imgClose.setVisibility(View.GONE);
        else {
            imgClose.setVisibility(View.VISIBLE);
            imgClose.setOnClickListener(cancleListener);
        }
        txtVersion.setText("V" + versionName);
        viewProgress.setVisibility(View.GONE);
        txtContent.setText(strContent);
        txtUpdate.setEnabled(true);
        txtUpdate.setVisibility(View.VISIBLE);
        txtUpdate.setText("立即更新");
        txtUpdate.setOnClickListener(updateBtnListener);
    }

    /**
     * 当检查新版本时，发现已经有了最新版本，更新dialog相关信息
     * @param listener
     */
    public void onResumeWhileDownloadSuccess(View.OnClickListener listener) {
        viewProgress.setVisibility(View.GONE);
        txtUpdate.setVisibility(View.VISIBLE);
        txtUpdate.setEnabled(true);
        txtUpdate.setText("点击安装");
        txtUpdate.setOnClickListener(listener);
    }

    /**
     * 开始下载时，初始化相关信息
     */
    public void startUpdate() {
        txtUpdate.setEnabled(false);
        txtUpdate.setVisibility(View.GONE);
        viewProgress.setVisibility(View.VISIBLE);
        txtProgress.setText("0%");
        progressbBar.setProgress(0);
    }

    /**
     * 在下载进程中，更新进度条...
     * @param progress
     */
    public void setProgress(int progress) {
        progressbBar.setProgress(progress);
        txtProgress.setText(progress + "%");
    }

    /**
     * 当下载完成时，初始化相关dialog信息
     */
    public void whenCompleted() {
        viewProgress.setVisibility(View.GONE);
        txtUpdate.setVisibility(View.VISIBLE);
        txtUpdate.setEnabled(false);
        txtUpdate.setText("下载完成");
    }

    /**
     * 当下载失败时，初始化相关dialog信息
     */
    public void whenFailed() {
        viewProgress.setVisibility(View.GONE);
        txtUpdate.setVisibility(View.VISIBLE);
        txtUpdate.setEnabled(true);
        txtUpdate.setText("立即更新");
    }
}
