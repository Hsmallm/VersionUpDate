package com.example.hzhm.versionupdate.utils.serves;

import android.util.Log;

import com.example.hzhm.versionupdate.model.ErrorBodyModel;
import com.google.gson.Gson;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * 功能描述：这个类主要用于错误码的转换，即将服务器请求返回的状态码转化为---》》枚举定义的相关标识，
 * 及其HttpErrorMsg定义的相关错误信息常量
 * instanceof ：运算符是用来在运行时指出对象是否是特定类的一个实例。instanceof通过返回一个布尔值来指出
 * Created by hzhm on 2016/8/18.
 */
public class ErrorConvertor {

    public static class Result{
        ServerResultCode code;
        String msg;
    }

    public static Result getResult(Throwable e){
        Result result = new Result();
        result.code = ServerResultCode.ACCESS_FAIL;
        result.msg = HttpErrorMsg.GSON_FORMAT_EXCEPTION;
        if(e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException){
            result.code = ServerResultCode.NETWORK_ERROR;
            result.msg = HttpErrorMsg.NETWORK_ERROR_TIPS;
        }else if(e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            String errorBodyString = httpException.response().errorBody().toString();
            int code = httpException.code();
            Log.e("errorBody", errorBodyString);
            Log.e("code", code + "");
            if (code >= 501 & code <= 505) {
                result.code = ServerResultCode.SERVER_DOWN;
                result.msg = HttpErrorMsg.UNKNOWN_ERROR_TIPS;
            } else if (code == 401) {
                result.code = ServerResultCode.NEED_LOGIN;
                result.msg = HttpErrorMsg.NEED_LOGIN;
                ;
            } else {
                ErrorBodyModel errorBodyModel = new Gson().fromJson(errorBodyString, ErrorBodyModel.class);
//                    if (code == 601) {
//                        result.code = ServerResultCode.MUST_RESET_LOGIN_PWD;
//                    } else
                if (code == 400) {
                    int myCode = errorBodyModel.getErrorModel().getCode();
                    if (24 == myCode)
                        result.code = ServerResultCode.IS_BINDED_WECHAT;
                    if (25 == myCode)
                        result.code = ServerResultCode.IS_BIND_TRC_ACCOUNT;
                    if(601 == myCode) //必须重置密码
                        result.code = ServerResultCode.MUST_RESET_LOGIN_PWD;
                }
                result.msg = errorBodyModel.getErrorModel().getDescription();
            }
        }
        return result;
    }

}
