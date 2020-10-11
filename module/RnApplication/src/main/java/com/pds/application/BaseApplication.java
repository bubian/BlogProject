package com.pds.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:07 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 在这里初始化相关sdk
 */
public class BaseApplication extends MultiDexApplication implements ReactApplication{
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
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }
}
