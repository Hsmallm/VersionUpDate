package com.example.hzhm.versionupdate.utils;

import android.content.SharedPreferences;

/**
 * Created by hzhm on 2017/1/9.
 */

public class UserConfig {

    private static final String TH_SHARED_PREFERENCES = "TH_SHARED_PREFERENCES_V3_0";
    private static final String USER_TOKEN = "userToken";

    /**
     * 保存用户Token
     *
     * @param token
     */
    public static void setUserToken(String token) {
        SharedPreferences prefs = ThApplication.getInstance().getSharedPreferences(TH_SHARED_PREFERENCES, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(USER_TOKEN, com.example.hzhm.versionupdate.utils.AESUtil.encrypt(token));
        prefsEditor.commit();
    }

    /**
     * 获取用户Token
     *
     * @return
     */
    public static String getUserToken() {
        SharedPreferences prefs = ThApplication.getInstance().getSharedPreferences(TH_SHARED_PREFERENCES, 0);
        return com.example.hzhm.versionupdate.utils.AESUtil.decrypt(prefs.getString(USER_TOKEN, ""));
    }
}
