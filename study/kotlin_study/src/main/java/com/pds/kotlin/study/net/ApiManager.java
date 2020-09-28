package com.pds.kotlin.study.net;

import com.blog.pds.net.RetrofitProvide;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/23 5:44 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public enum ApiManager {

    INSTANCE;

    private static AppApi sAppApi;

    /**
     * 用户相关数据Api
     */
    public static AppApi getApi() {
        if (sAppApi == null) {
            sAppApi = RetrofitProvide.getInstance()
                    .build(RetrofitProvide.getInstance().buildRetrofit2(ApiEnv.getAppEnv()),
                            AppApi.class);
        }
        return sAppApi;
    }
}
