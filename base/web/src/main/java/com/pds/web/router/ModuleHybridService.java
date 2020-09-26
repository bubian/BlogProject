package com.pds.web.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.pds.web.param.HybridParamLog;
import com.pds.web.param.HybridParamPayInfo;
import com.pds.web.param.HybridParamShare;
import com.pds.web.param.HybridParamUpload;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;
import java.util.Observer;

public interface ModuleHybridService extends IProvider {

    /**
     * 获取网络请求公共参数
     *
     * @return
     */
    HashMap<String, String> getNetworkCommonParams();

    void pop(int num, Class<?> hybridActivityClass);

    Activity getCurrentActivity();

    void showToastMessage(String text);

    void payInterceptorWithUrl(String url, Observer observer);

    HashMap<String, Object> getJsInterface(WebView webView, Activity activity);

    void onDestroyHybrid();

    String getUserSession();

    String getAppName(Context context);

    String getPayUrl();

    String getBaseUrl();

    void startPay(HybridParamPayInfo paramPayInfo, Observer onPayCallBack);

    void startShare(WebView webView, HybridParamShare paramShare, Observer onShareCallBack);

    String getDeviceId(Context context);

    int getScreenWidth();

    int getScreenHeight();

    /**
     * 跳转到应用市场
     *
     * @param context
     */
    void toAppStore(Context context);

    void saveImgToLocal(final Context context, final View v, final boolean isShowToast);

    void onActivityResultSelectedPhotos(HybridParamUpload msg, Intent intent);

    void gotoSelectPhoto(Activity activity, int photoCount, int requestCode);

    void gotoLogin(Activity activity, HybridParamLog loginParam, int requestCode);

    void configImmersiveMode(Activity activity);
}
