package com.pds.tools.core.dokit.envswitch;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil;
import com.pds.tools.business.med.env.PatientEnv;
import com.pds.tools.common.cache.PreferencesKey;
import com.pds.tools.common.cache.PreferencesManager;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/30 11:50 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

public class EnvInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originRequest = chain.request();
        String contentType = originRequest.header("Content-Type");
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(contentType)) {
            return chain.proceed(originRequest);
        }
        HttpUrl originUrl = originRequest.url();
        String host = originUrl.host();

        if (!host.startsWith("patient-medication")){
            return chain.proceed(originRequest);
        }

        HttpUrl.Builder builder = originUrl.newBuilder();

        boolean envSwitch = PreferencesManager.getBoolean(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_ENV_SWITCH);
        if (envSwitch){
            int envD = PreferencesManager.getInt(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_DEFAULT_ENV);
            String newH = PatientEnv.getAppEnv(envD);
            HttpUrl newU = HttpUrl.parse(newH);
            builder.scheme(newU.scheme());
            builder.host(newU.host());
        }else {
            String customH = PreferencesManager.getString(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_CUSTOM_ENV);
            HttpUrl newU = HttpUrl.parse(customH);
            builder.scheme(newU.scheme());
            builder.host(newU.host());
        }
        Request newRequest = originRequest.newBuilder().url(builder.build()).build();
        return chain.proceed(newRequest);
    }
}
