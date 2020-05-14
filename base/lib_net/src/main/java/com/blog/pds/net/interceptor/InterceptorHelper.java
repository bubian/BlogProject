package com.blog.pds.net.interceptor;

import okhttp3.Interceptor;

public class InterceptorHelper {
    private static final InterceptorHelper INSTANCE = new InterceptorHelper();
    private final Interceptor PARAMS_INTERCEPTOR = new ParamsInterceptor();
    private final Interceptor CACHE_INTERCEPTOR = new CacheInterceptor();
    private final Interceptor HEADER_INTERCEPTOR = new HeaderParamsInterceptor();

    private InterceptorHelper() {
    }

    public static InterceptorHelper getInstance() {
        return INSTANCE;
    }

    public Interceptor getParamsInterceptor() {
        return PARAMS_INTERCEPTOR;
    }

    public Interceptor getCacheInterceptor() {
        return CACHE_INTERCEPTOR;
    }

    public Interceptor getHeaderParamsInterceptor() {
        return HEADER_INTERCEPTOR;
    }

}
