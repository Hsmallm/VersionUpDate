package com.example.hzhm.versionupdate.utils.serves;

import com.example.hzhm.versionupdate.BuildConfig;
import com.example.hzhm.versionupdate.utils.ThApplication;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

/**
 * TODO
 * Created by hzhm on 2016/8/22.
 */
public class HttpClient {
    private static OkHttpClient instance;
    private static PersistentCookieStore persistentCookieStore;

    public static OkHttpClient getInstance() {
        synchronized (HttpClient.class) {
            if (instance == null) {
                persistentCookieStore = new PersistentCookieStore(ThApplication.getInstance());
//                String appVersion = BuildConfig.VERSION_NAME;
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .cookieJar(CookieJar.NO_COOKIES)
                        .cookieJar(new CustomCookieJar(new CookieManager(persistentCookieStore, CookiePolicy.ACCEPT_ALL)))
                        .addInterceptor(new HeaderConfigInterceptor());
                if(BuildConfig.DEBUG){
                    builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
                }
                instance = builder.build();
            }
            return instance;
        }
    }

    public static void clearCookie() {
        if (instance != null && persistentCookieStore != null) {
            persistentCookieStore.removeAll();
        }
    }
}