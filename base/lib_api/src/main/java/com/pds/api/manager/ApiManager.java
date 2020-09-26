package com.pds.api.manager;

import com.blog.pds.net.RetrofitManager;
import com.pds.api.IPagingApi;
import com.pds.api.IUserApi;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description
 * @time 2016-12-13-17:13
 */
public class ApiManager {

    private static IUserApi mUserApi;
    private static IPagingApi mPagingApi;

    public static IUserApi getUserApi() {
        if (mUserApi == null) {
            mUserApi = RetrofitManager.getInstance().build(IUserApi.class);
        }
        return mUserApi;
    }

    public static IPagingApi getPagingApi() {
        if (mPagingApi == null) {
            mPagingApi = RetrofitManager.getInstance().build(IPagingApi.class);
        }
        return mPagingApi;
    }
}
