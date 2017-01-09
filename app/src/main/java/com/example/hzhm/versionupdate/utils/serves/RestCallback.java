package com.example.hzhm.versionupdate.utils.serves;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

/**
 * 功能描述：定义一个抽象类实现相关请求结果的回调
 * 注：1、这个抽象类中定义相关的抽象方法，抽象类中的抽象方法必须被实现
 *     2、抽象类不能被实例化,但是可以作为参数传递
 *     3、抽象方法只需声明，而不需实现某些功能。
 * //注：在这里我们启用实现retrofit.Callback接口的两个方法：onResponse、onFailure（注：在这里Callback回调的这个两个方法已经完成异步，回到主线程了）
 * Created by hzhm on 2016/8/18.
 */
public abstract class RestCallback<T> implements Callback<T>, com.example.hzhm.versionupdate.utils.serves.Callback<T>{

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        //请求成功时
        if(response.isSuccessful()){
            HttpHelper.onSuccess(this,response.body());
        //请求失败时
        }else {
            //new HttpException(response)：为实例化一个Throwable对象
            ErrorConvertor.Result result = ErrorConvertor.getResult(new HttpException(response));
            HttpHelper.onFail(this,result.code,result.msg);
        }

    }

    //请求失败时
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        ErrorConvertor.Result result = ErrorConvertor.getResult(t);
        HttpHelper.onFail(this,result.code,result.msg);
    }

    public abstract  void onSuccess(T model);

    public abstract void onFail(ServerResultCode serverResultCode, String errorMessage);
}
