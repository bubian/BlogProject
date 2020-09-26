package com.pds.api;

import com.pds.entity.PagingEntity;
import com.pds.entity.base.BaseListEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 1:32 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public interface IPagingApi {
    @GET("/rest/v1/get/page")
    Observable<BaseListEntity<PagingEntity>> getPageList();
}
