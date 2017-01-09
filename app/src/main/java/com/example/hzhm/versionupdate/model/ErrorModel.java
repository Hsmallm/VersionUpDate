package com.example.hzhm.versionupdate.model;

/**
 * Created by hzhm on 2017/1/9.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 功能描述：定义错误信息的实体类
 * Created by hzhm on 2016/8/18.
 */
public class ErrorModel {

    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("_tx_third")
    @Expose
    private String txThird;

    @SerializedName("description")
    @Expose
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTxThird() {
        return txThird;
    }

    public void setTxThird(String txThird) {
        this.txThird = txThird;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}