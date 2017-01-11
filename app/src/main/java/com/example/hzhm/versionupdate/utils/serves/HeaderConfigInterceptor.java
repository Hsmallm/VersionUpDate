package com.example.hzhm.versionupdate.utils.serves;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.compat.BuildConfig;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.hzhm.versionupdate.utils.AppConfig;
import com.example.hzhm.versionupdate.utils.ThApplication;
import com.example.hzhm.versionupdate.utils.UserConfig;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hzhm on 2016/8/22.
 */
public class HeaderConfigInterceptor implements Interceptor{


    private static int screenHeight;
    private static int screenWidth;
    private static String deveiceId;
    private static String provider;
    private static String osVersion;
    private static String channel;

    private static void init() {
        try {
            Context c = ThApplication.getInstance();
            screenWidth = c.getResources().getDisplayMetrics().widthPixels;
            screenHeight = c.getResources().getDisplayMetrics().heightPixels;
            TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            provider = telephonyManager.getNetworkOperatorName();
            osVersion = Build.VERSION.CODENAME;
            deveiceId = telephonyManager.getDeviceId();
            ApplicationInfo appInfo = c.getPackageManager().getApplicationInfo(com.example.hzhm.versionupdate.BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void configHeader(okhttp3.Request.Builder builder) {
        try {
            addHeader(builder, "Token", UserConfig.getUserToken());//重要
            addHeader(builder, "ostype", "A");//重要
            addHeader(builder, "versions-info", BuildConfig.VERSION_NAME);//重要
            addHeader(builder, "channel", channel);
            addHeader(builder, "screenheight", screenHeight);
            addHeader(builder, "screenwidth", screenWidth);
            addHeader(builder, "mobilemod", Build.MODEL);
            addHeader(builder, "uniquecode", deveiceId);
            addHeader(builder, "osversion", osVersion);
            addHeader(builder, "provider", URLEncoder.encode(provider, "UTF-8"));
            addHeader(builder, "mobilefac", URLEncoder.encode(Build.MANUFACTURER, "UTF-8"));
            addHeader(builder, "umeng-push-id", AppConfig.getPushId());
            addHeader(builder, "deviceToken", AppConfig.getPushId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addHeader(okhttp3.Request.Builder builder, String key, Object value) {
        try {
            if (null != value) {
                builder.removeHeader(key);
                builder.addHeader(key, String.valueOf(value));
            }
        } catch (Exception e) {
//            LogUtil.e(e);
            Log.e("Tag",e+"");
        }
    }

    public HeaderConfigInterceptor() {
        init();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();
        configHeader(builder);
        final Request requestWithUserAgent = builder.build();
        return chain.proceed(requestWithUserAgent);
    }
}
