package com.example.hzhm.versionupdate.utils.serves;

import android.net.Uri;

import com.example.hzhm.versionupdate.BuildConfig;

/**
 * Created by hzhm on 2016/8/22.
 *
 * 功能描述：服务器地址的相关配置...
 */
public class ServerConfig {

    //正式环境域名(主机地址)
    public static final String NORMAL_BASE_URL_TRC = "http://www.trc.com/trcapi/v1/";//泰然城服务器
    public static final String NORMAL_BASE_TAIHE = "http://jr.m.trc.com/";//泰和网理财域名
    public static final String NORMAL_BASE_URL_TAIHE = NORMAL_BASE_TAIHE + "clientInterface.do";//泰和网理财服务器
    public static final String NORMAL_BASE_URL_ACCOUNT = "http://passport.trc.com:82/account/";//帐户认证中心服务器
    public static final String NORMAL_BASE_URL_QINIU = "http://passport.trc.com:82/qiniu/";//帐户认证中心服务器
    public static final String NORMAL_BASE_URL_HUAXING = "http://cg.trc.com/trcloanweb/";//存管帐户服务器
    public static final String NORMAL_VALIDE_CODE_PREFIX = "http://passport.trc.com:82/captcha/captcha.jpeg?id=";//图形验证码链接前缀

    //测试环境域名(主机地址)
    public static final String TEST_BASE_URL_TRC = "http://app.trc.com/trcapi/v1/";//泰然城服务器
    public static final String TEST_BASE_TAIHE = "http://172.30.250.131/"; //泰和网域名
    public static final String TEST_BASE_URL_TAIHE = TEST_BASE_TAIHE + "clientInterface.do";//泰和网理财服务器
    public static final String TEST_BASE_URL_ACCOUNT = "http://172.30.251.114:8080/account/";//F帐户认证中心服务器
    public static final String TEST_BASE_URL_QINIU = "http://172.30.251.114:8080/qiniu/";//帐户认证中心服务器
    public static final String TEST_BASE_URL_HUAXING = "http://172.30.250.128:8080/trcloanweb/";//存管帐户服务器
    public static final String TEST_VALIDE_CODE_PREFIX = "http://172.30.251.114:8080/captcha/captcha.jpeg?id=";//图形验证码链接前缀


    //正式环境域名(主机地址)
    public static String BASE_URL_TRC = TEST_BASE_URL_TRC;//泰然城服务器
    public static String BASE_TAIHE = TEST_BASE_TAIHE;//泰和网理财域名
    public static String BASE_URL_TAIHE = TEST_BASE_URL_TAIHE;//泰和网理财服务器
    public static String BASE_URL_ACCOUNT = TEST_BASE_URL_ACCOUNT;//帐户认证中心服务器
    public static String BASE_URL_QINIU = TEST_BASE_URL_QINIU;//帐户认证中心服务器
    public static String BASE_URL_HUAXING = TEST_BASE_URL_HUAXING;//存管帐户服务器
    public static String BASE_URL_VALIDE_CODE_PREFIX = TEST_VALIDE_CODE_PREFIX;//图形验证码链接前缀

    public static String getTrcBaseUrl() {
        return BuildConfig.DEBUG ? BASE_URL_TRC : NORMAL_BASE_URL_TRC;
    }

    public static String getTrcHost() {
        Uri parse = Uri.parse(getTrcBaseUrl());
        return parse.getScheme() + "://" + parse.getHost();
    }

    public static String getAccountBaseUrl() {
        return BuildConfig.DEBUG ? BASE_URL_ACCOUNT : NORMAL_BASE_URL_ACCOUNT;
    }

    public static String getAccountQiniuUrl() {
        return BuildConfig.DEBUG ? BASE_URL_QINIU : NORMAL_BASE_URL_QINIU;
    }

    public static String getAccountValideCodeUrl(String uuid) {//图形验证码
        String urlPrefix = BuildConfig.DEBUG ? BASE_URL_VALIDE_CODE_PREFIX : NORMAL_VALIDE_CODE_PREFIX;
        return urlPrefix + uuid;
    }

    public static String getTaiheBaseUrl() {
        return BuildConfig.DEBUG ? BASE_URL_TAIHE : NORMAL_BASE_URL_TAIHE;
    }

    public static String getTaiheDomain() {
        return BuildConfig.DEBUG ? BASE_TAIHE : NORMAL_BASE_TAIHE;
    }

    public static String getHuaxingBaseUrl() {
        return BuildConfig.DEBUG ? BASE_URL_HUAXING : NORMAL_BASE_URL_HUAXING;
    }
}
