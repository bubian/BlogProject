package com.blog.pds.net;

import com.blog.pds.net.api.IUserApi;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description
 * @time 2016-12-13-17:13
 */
public enum ApiManager {

    INSTANCE;

    private static IUserApi mUserApi;

    ApiManager() { }

    public static IUserApi getUserApi() {
        if (mUserApi == null) {
            mUserApi = RetrofitManager.getInstance().build(IUserApi.class);
        }
        return mUserApi;
    }
}
