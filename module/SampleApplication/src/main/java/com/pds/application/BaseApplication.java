package com.pds.application;

import android.content.Context;
import androidx.multidex.MultiDexApplication;
import com.pds.sample.application.ModuleApplication;
import java.lang.reflect.InvocationHandler;

import dagger.hilt.android.HiltAndroidApp;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:07 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 在这里初始化相关sdk
 */

@HiltAndroidApp
public class BaseApplication extends MultiDexApplication {
    private static BaseApplication sApplication;
    private InvocationHandler mHandler;

    public static BaseApplication getApplication() {
        return sApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        ModuleApplication.instance().onCreate(sApplication);
    }
}
