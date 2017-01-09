package com.example.hzhm.versionupdate.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by hzhm on 2017/1/9.
 * <p>
 * Description：封装Taost，频繁出发show（）时，只显示最新的Toast 防止消息过多，Toast长时间至所有消息释放完毕
 */
public class ToastUtil {
    private static Toast toast = null;

    public static void showNormalToast(final String message) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast(message);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    toast(message);
                }
            });
        }

    }

    private static void toast(String message) {
        try {
            if (toast != null) {
                toast.cancel();
            }
            if (TextUtils.isEmpty(message)) return;
            toast = Toast.makeText(ThApplication.getInstance(), message, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelTosat() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
