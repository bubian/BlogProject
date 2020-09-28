package com.blog.pds.net;

import android.os.Environment;
import android.util.Log;

import com.blog.pds.net.interceptor.HttpLoggingInterceptor;
import com.blog.pds.net.interceptor.InterceptorHelper;
import com.blog.pds.net.interceptor.OkHttpLogInterceptor;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author hmy
 */
public class RetrofitProvide {

    private static final long CONNECT_TIMEOUT = 30L;
    private static final long READ_TIMEOUT = 30L;
    private static final long WRITE_TIMEOUT = 30L;
    private static final long MAX_CACHE_SIZE = 5 * 1024 * 1024L;

    private static RetrofitProvide sInstance;

    /**
     *
     */
    public static synchronized RetrofitProvide getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitProvide();
        }
        return sInstance;
    }

    private OkHttpClient buildDefaultClient(boolean useHeaderParams) {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
//        builder.cache(new Cache(FileUtil.getNetCacheDir(), MAX_CACHE_SIZE));
        builder.retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
//            builder.addNetworkInterceptor(new StethoInterceptor());
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.e("HttpResponse:", message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
//            builder.addNetworkInterceptor(new OkHttpLogInterceptor(true));
        } else { //  2017/8/28 如果是qa要打release包请注释掉下面代码
            //新增，如果不是线上https取消配置
            if (BuildConfig.API_URL_TYPE == 3) {
                builder.connectionSpecs(Collections.singletonList(spec)).protocols(Collections.singletonList(Protocol.HTTP_1_1));
            }
        }
        if (useHeaderParams) {
            builder.addInterceptor(InterceptorHelper.getInstance().getHeaderParamsInterceptor());
        } else {
            builder.addInterceptor(InterceptorHelper.getInstance().getParamsInterceptor());
        }
        builder.addInterceptor(InterceptorHelper.getInstance().getCacheInterceptor());
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addNetworkInterceptor(InterceptorHelper.getInstance().getCacheInterceptor());

        builder.cache(new Cache(Environment.getDataDirectory(), MAX_CACHE_SIZE));


        return builder.build();
    }

    /**
     *
     */
    public Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(buildDefaultClient(false))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 公共参数放到header中
     */
    public Retrofit buildRetrofit2(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(buildDefaultClient(true))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * @param retrofit
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T build(Retrofit retrofit, Class<T> cls) {
        if (retrofit == null) {
            throw new NullPointerException("retrofit is null");
        }
        return retrofit.create(cls);
    }
}
