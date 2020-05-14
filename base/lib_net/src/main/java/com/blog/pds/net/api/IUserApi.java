package com.blog.pds.net.api;

import com.pds.entity.BaseEntity;
import com.pds.entity.DataEntity;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IUserApi {

    @GET("/rest/v1/users/qrcode")
    Observable<BaseEntity<DataEntity>> getUserQrCode(@Query("wxPlatform") String wxPlatform);

    @POST("/rest/v1/users/flash-login-phone")
    @FormUrlEncoded
    Observable<BaseEntity<DataEntity>> getPhoneNumber(@Field("flash_data") String flash_data);

}
