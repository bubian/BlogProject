package com.blog.pds.net.interceptor;

import android.os.Build;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(getRequest(chain.request()));
    }

    private Request getRequest(Request request) {
        Request.Builder builder = request.newBuilder();
        builder
                .header("sys_p", "a")
                .header("x_platform", "a")
                .header("sys_m", Build.MODEL)
                .header("sys_v", Build.VERSION.RELEASE)
                .header("sys_vc", String.valueOf(Build.VERSION.SDK_INT));
        return builder.build();
    }
}
