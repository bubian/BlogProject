package com.blog.pds.net;

import com.pds.env.blog.AppEnv;
import retrofit2.Retrofit;

/**
 * Author: KindyFung.
 * CreateTime:  2016/12/5 14:37
 * Email：fangjing@medlinker.com.
 * Description: Retrofit+okHttp
 */

public class RetrofitManager {
    private static RetrofitManager mInstance;
    private static Retrofit mRetrofit;

    private RetrofitManager() {
    }

    public static synchronized RetrofitManager getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitManager();
        }
        return mInstance;
    }


    private void buildRetrofit() {
        mRetrofit = RetrofitProvide.getInstance().buildRetrofit(AppEnv.getAppEnv());
    }

    private Retrofit getRetrofit() {
        if (mRetrofit == null) {
            buildRetrofit();
        }
        return mRetrofit;
    }

    public <T> T build(Class<T> cls) {
        return build(getRetrofit(), cls);
    }

    public <T> T build(Retrofit retrofit, Class<T> cls) {
        if (retrofit == null) {
            throw new NullPointerException("retrofit is null");
        }
        return retrofit.create(cls);
    }
}
