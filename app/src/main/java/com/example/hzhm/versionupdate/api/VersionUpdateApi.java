package com.example.hzhm.versionupdate.api;

import com.example.hzhm.versionupdate.BuildConfig;
import com.example.hzhm.versionupdate.model.VersionUpdateModel;
import com.example.hzhm.versionupdate.utils.serves.HttpClient;
import com.example.hzhm.versionupdate.utils.serves.RestCallback;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hzhm on 2017/1/9.
 */

public class VersionUpdateApi {

    public static Retrofit retrofit;
    public static VersionUpdateServes serves;


    //实例化Retrofit对象
    public static void init() {
        retrofit = new Retrofit.Builder()
                .client(HttpClient.getInstance())
                .baseUrl("http://172.30.250.111/")
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //实例化AddressServes对象/这里采用的是Java的动态代理模式
        serves = retrofit.create(VersionUpdateServes.class);
    }

    /**
     * 版本更新
     * @param callback
     */
    public static void getVersionUpdater(RestCallback<VersionUpdateModel> callback) {
        String versionName = BuildConfig.VERSION_NAME;
        String platformId = "id_695E61DCBE8A4F2BA256D3B76AD5D392-" + "3.0.0.0";
        serves.getVersionUpdateInfos(platformId).enqueue(callback);
    }
}
