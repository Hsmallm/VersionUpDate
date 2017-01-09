package com.example.hzhm.versionupdate.api;

import com.example.hzhm.versionupdate.model.VersionUpdateModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hzhm on 2017/1/9.
 */
public interface VersionUpdateServes {

    @GET("operation/apk/{platformId}")
    Call<VersionUpdateModel> getVersionUpdateInfos(@Path("platformId") String addressId);
}
