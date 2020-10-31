package com.pds.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;

import java.lang.reflect.InvocationHandler;
import com.pds.main.ModuleApplication;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:07 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 在这里初始化相关sdk
 */
public class BaseApplication extends MultiDexApplication {
    private static BaseApplication sApplication;
    private InvocationHandler mHandler;

    public static BaseApplication getApplication() {
        return sApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ModuleApplication.instance().attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        ModuleApplication.instance().onCreate(sApplication);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }

    /**
     * 动态代理
     */
//    private void initProxy() {
//        mHandler = (proxy, method, args) -> {
//            // If the method is a method from Object then defer to normal invocation.
//            Log.e("======","initProxy = " + method.getDeclaringClass());
//            if (method.getDeclaringClass() == IApplication.class) {
//                Log.e("======","initProxy1221 = "+ this);
//                Object o =  method.invoke(this, args);
//                Log.e("======","initProxy11 = "+ o);
//                return o;
//            }
//            return null;
//        };
//        mSubApplication = (IApplication) Proxy.newProxyInstance(
//                IApplication.class.getClassLoader(), // 传入ClassLoader
//                new Class[]{IApplication.class}, // 传入要实现的接口
//                mHandler); // 传入处理调用方法的InvocationHandler
//    }
}
