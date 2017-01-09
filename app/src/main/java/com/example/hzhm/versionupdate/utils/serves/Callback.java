package com.example.hzhm.versionupdate.utils.serves;

/**
 * Created by hzhm on 2016/8/18.
 */
public interface Callback<T> {

    void onSuccess(T model);

    void onFail(ServerResultCode serverResultCode, String errorMessage);
}
