package com.example.hzhm.versionupdate.utils;

import android.app.Application;
import android.content.Context;
//import com.unknown.umengshare.UmengShareConfig;

/**
 * 功能描述：Application：1、和Activity、Service、ContentProvider一样都是属于android框架的一系统组建，每当android程序启动时，系统就会自动帮我们创建一个Application，
 * 它注意用于存储一些系统信息；
 * 2、如果需要创建自己 的Application，也很简单创建一个类继承 Application并在manifest的application标签中进行注册(只需要给Application标签增加个name属性把自己的 Application的名字定入即可)。
 * 3、Application可以说是单例 (singleton)模式的一个类.且application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。
 * Created by hzhm on 2016/6/13.
 */
public class ThApplication extends Application {

    private static Context context;
    /**
     * 应用及设备信息
     */
    public static Context getInstance() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在ThApplication里面初始化友盟分享相关设置，使其具有这个程序的生命周期(类型与缓存)
//        UmengShareConfig.init(this);
//        ObjCacheUtil.init(this);
        //注：getApplicationContext() 生命周期是整个应用，应用摧毁它才摧毁 Activity.this的context属于activity ，activity 摧毁他就摧毁
             //activity.this要返回一个activity，而getApplicationContext()就不一定返回一个activity
        context = getApplicationContext();
    }
}
