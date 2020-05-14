package com.blog.pds.net.interceptor;
import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final String CACHE_CONTROL_ONLY_CACHE = "public, only-if-cached, max-age=2419200";
    private static final String CACHE_CONTROL_NO_CACHE = "public, max-age=1";
    private static final String HEADER_USER_CACHE_TYPE = "User-Cache-Type";
    // 断网情况下，加载缓存，联网情况下，优先加载缓存，默认情况
    public static final String TYPE_NETWORK_WITH_CACHE = "network_with_cache";
    // 断网情况下，加载缓存，联网情况下，只加载网络
    public static final String TYPE_NETWORK_NO_CACHE = "network_no_cache";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        String cacheControl = request.cacheControl().toString();
//        if (TextUtils.isEmpty(cacheControl)) {
//            return chain.proceed(request);
//        }
        if (!isNetworkConnected()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        String cacheControl;
        Response originalResponse = chain.proceed(request);
        if (!isNetworkConnected()) {
            cacheControl = CACHE_CONTROL_ONLY_CACHE;
        } else {
            cacheControl = CACHE_CONTROL_NO_CACHE;
        }
        return originalResponse.newBuilder()
                .header(HEADER_CACHE_CONTROL, cacheControl)
                .removeHeader(HEADER_PRAGMA)
                .build();
    }

    private boolean isNetworkConnected() {
//        return NetworkUtil.isConnected(BaseApplication.getApplication());
        return true;
    }

}
