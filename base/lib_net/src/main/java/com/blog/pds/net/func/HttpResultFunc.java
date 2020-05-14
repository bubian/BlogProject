package com.blog.pds.net.func;

import android.os.Parcel;

import com.blog.pds.net.exception.ApiException;
import com.pds.entity.BaseEntity;
import com.pds.entity.DataEntity;

import io.reactivex.rxjava3.functions.Function;


public class HttpResultFunc<T extends DataEntity> implements Function<BaseEntity<T>, T> {
    @Override
    public T apply(BaseEntity<T> httpResult) throws Exception {
        if (!httpResult.isSuccess()) {
            httpResult.setMsg("");
            if (httpResult.getCode() == 20004) {

            }
            throw new ApiException(httpResult.getCode(), httpResult.getMsg());
        }
        return httpResult.getData() == null ? (T) T.CREATOR.createFromParcel(Parcel.obtain()) : httpResult.getData();
    }
}
