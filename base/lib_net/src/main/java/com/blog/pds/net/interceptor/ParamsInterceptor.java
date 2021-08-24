package com.blog.pds.net.interceptor;

import android.os.Build;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = addDefaultParams(chain.request());
        return chain.proceed(request);
    }

    private Request addDefaultParams(Request request) {
        HttpUrl.Builder builder = request.url().newBuilder()
                .addQueryParameter("sys_p", "a")
                .addQueryParameter("x_platform", "a")
                .addQueryParameter("sys_m", Build.MODEL)
                .addQueryParameter("sys_v", Build.VERSION.RELEASE)
                .addQueryParameter("sys_vc", String.valueOf(Build.VERSION.SDK_INT));
        HttpUrl httpUrl = builder.build();
        return request.newBuilder().url(httpUrl)
                .build();
    }
}
