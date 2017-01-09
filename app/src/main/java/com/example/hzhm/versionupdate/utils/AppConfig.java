package com.example.hzhm.versionupdate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hzhm.versionupdate.BuildConfig;

/**
 * Created by hzhm on 2017/1/9.
 */

public class AppConfig {

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";
    private static final String KEY4 = "key4";
    private static final String KEY5 = "key5";
    private static final String XML_NAME = "AppConfig-v1";
    private static final String KEY6 = "key6";
    private static final String KEY7 = "key7";
    //省市区列表etag
    private static final String AREA_ETAG = "area_aTag";


    private static SharedPreferences getSharedPreferences() {
        return ThApplication.getInstance().getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
    }

    public static final String getPushId() {
        return getSharedPreferences().getString(KEY5, null);
    }

    public static void setPushId(String pushId) {
        getSharedPreferences().edit().putString(KEY5, pushId).apply();
    }

    /**
     * Umeng自定义事件统计是否是安装后首次使用（或清除数据后首次使用）
     */
    public static boolean isFirstUseAfterInstallForUmeng() {
        return getSharedPreferences().getBoolean(KEY1, true);
    }

    /**
     * 设置Umeng自定义事件统计是否是安装后首次使用（或清除数据后首次使用）
     */
    public static void setIsFirstUseAfterInstallForUmeng(boolean isFirst) {
        getSharedPreferences().edit().putBoolean(KEY1, isFirst).apply();
    }

    public static void clear() {
        getSharedPreferences().edit().clear().commit();
    }

    /**
     * 版本名不一致时需要显示划屏引导页面（三种情况：新安装，数据被清空，升级后）
     */
    public static boolean showGuidePage() {
        String oldVersionName = getSharedPreferences().getString(KEY2, null);
        getSharedPreferences().edit().putString(KEY2, BuildConfig.VERSION_NAME).apply();
        return !BuildConfig.VERSION_NAME.equals(oldVersionName);
    }

    /**
     * @return 返回插屏广告的图片是否准备就绪
     */
    public static boolean isStartAdPicReady() {
        return getSharedPreferences().getBoolean(KEY3, false);
    }

    /**
     * @param isReady 设置插屏广告的图片是否准备就绪
     */
    public static void setStartAdPicReady(boolean isReady) {
        getSharedPreferences().edit().putBoolean(KEY3, isReady).apply();
    }

    /**
     * @return 是否Toast HTTP日志
     * 参考{@link #setShowHttpRequestToast(boolean)} (Context)}
     */
    public static final boolean shouldShowHttpRequestToast() {
        return getSharedPreferences().getBoolean(KEY6, false);
    }

    /**
     * 是否Toast HTTP日志
     * 参考{@link #shouldShowHttpRequestToast()} (Context)}
     */
    public static void setShowHttpRequestToast(boolean showOrNot) {
        getSharedPreferences().edit().putBoolean(KEY6, showOrNot).apply();
    }

    /**
     * @return 是否打印HTTP日志
     * <P/> 参考{@link #setPrintHttpRequestToast(boolean)} (Context)}
     */
    public static final boolean shouldPrintHttpRequestToast() {
        return getSharedPreferences().getBoolean(KEY7, false);
    }

    /**
     * 是否打印HTTP日志
     * <P/> 参考{@link #shouldPrintHttpRequestToast()}
     */
    public static void setPrintHttpRequestToast(boolean showOrNot) {
        getSharedPreferences().edit().putBoolean(KEY7, showOrNot).apply();
    }


    /**
     * 保存省市区列表eTag
     *
     * @param eTag
     */
    public static void setAreaTag(String eTag) {
        getSharedPreferences().edit().putString(AREA_ETAG, eTag).apply();
    }

    /**
     * 获取省市区列表eTag
     *
     * @return
     */
    public static String getAreaTag() {
        return getSharedPreferences().getString(AREA_ETAG, "");
    }
}
