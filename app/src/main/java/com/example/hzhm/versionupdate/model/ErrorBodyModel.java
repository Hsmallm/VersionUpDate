package com.example.hzhm.versionupdate.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 功能描述：定义错误信息的实体类
 * Created by hzhm on 2016/8/18.
 */
public class ErrorBodyModel {

    @SerializedName("error")
    @Expose
    private ErrorModel errorModel;

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }
}
