package com.blog.pds.net.interceptor;

import android.os.Build;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author <a href="mailto:ganyu@medlinker.com">ganyu</a>
 * @version 1.0
 * @description 默认网络请求参数拦截器
 * @time 2016/10/24 15:50
 */
public class ParamsInterceptor implements Interceptor {
//    // 客户端应用版本号
//    private static String mVersionName;
//    // 手机设备号
//    private String mChannelId;
//    private String mClientId;

    @Override
    public Response intercept(Chain chain) throws IOException {
//        initRequestParams();
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
