package com.pds.web;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class X5SDK {

    private static final String TAG = "X5SDK";

    public static void init(Context context) {
        new Handler().postDelayed(() -> {
            preInit(context);
        }, 1000);
    }

    public static void check(Context context){
        if (!QbSdk.isTbsCoreInited()){
            preInit(context);
        }
    }

    private static void preInit(Context context) {
        // XfiveWebviewCookie.initCookie(context);
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        preX5(context);
    }

    private static void preX5(Context context) {
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "x5 core onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "x5 core onViewInitFinished result = " + b);
            }
        });
    }

    private void X5Download(){
        QbSdk.setTbsListener(
                new TbsListener() {
                    @Override
                    public void onDownloadFinish(int i) {
                        Log.d("QbSdk", "onDownloadFinish -->下载X5内核完成：" + i);
                    }

                    @Override
                    public void onInstallFinish(int i) {
                        Log.d("QbSdk", "onInstallFinish -->安装X5内核进度：" + i);
                    }

                    @Override
                    public void onDownloadProgress(int i) {
                        Log.d("QbSdk", "onDownloadProgress -->下载X5内核进度：" + i);
                    }
                });
    }
}
