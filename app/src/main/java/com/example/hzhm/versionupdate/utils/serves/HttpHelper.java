package com.example.hzhm.versionupdate.utils.serves;

/**
 * 功能描述：服务器响应后的异常处理try/catch
 * Created by hzhm on 2016/8/19.
 */
public class HttpHelper {

    public static<T> void onSuccess(final Callback<T> callback, final T t){
        try {
            callback.onSuccess(t);
        } catch (Exception e) {
//            LogUtil.e("this", Log.getStackTraceString(e));
        }
    }

    public static void onFail(final Callback callback, final ServerResultCode serverResultCode, final String error){
        try {
            callback.onFail(serverResultCode,error);
        } catch (Exception e) {
//            LogUtil.e("this", Log.getStackTraceString(e));
        }
    }
}
