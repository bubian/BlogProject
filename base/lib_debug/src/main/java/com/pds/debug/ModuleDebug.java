package com.pds.debug;

import android.app.Application;
import android.os.Handler;

import com.pds.storage.mmkv.MMKVManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 11:58 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleDebug {
    public static final boolean DEBUG = false;

    private Application mApplication;

    private static Handler sAppMainHandler;

    /**
     * 不一定要在启动的时候初始化，根据业务而定
     * @param application
     */
    public static void init(Application application) {
        instance().mApplication = application;
        sAppMainHandler = new Handler(application.getMainLooper());
    }

    public static final ModuleDebug instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final ModuleDebug INSTANCE = new ModuleDebug();
    }

    public Application appContext() {
        if (null == mApplication) {
            mApplication = findApplicationFromApp();
        }

        if (null == mApplication){
            mApplication = findApplicationFromSystem();
        }

        if (null == mApplication){
            throw new NullPointerException("ModuleStorage appContext is null");
        }
        return mApplication;
    }

    private Application findApplicationFromApp() {
        try {
            Class<?> clazz = Class.forName(BuildConfig.APPLICATION_CLASS_NAME);
            Field field = clazz.getField(BuildConfig.APPLICATION_VAR_NAME);
            field.setAccessible(true);
            Object app = field.get(clazz);
            if (app instanceof Application) {
                return (Application) app;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Application findApplicationFromSystem() {
        try {
            Method method = Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
            method.setAccessible(true);
            Object at = method.invoke(null);
            Object app = at.getClass().getMethod("getApplication").invoke(at);
            if (app instanceof Application) {
                return (Application) app;
            }
        } catch (Exception ignored) {}
        return null;
    }

    public void postToMainLooper(Runnable runnable) {
        sAppMainHandler.post(runnable);
    }
    public void postDelayToMainLooper(Runnable runnable, long ms) {
        sAppMainHandler.postDelayed(runnable, ms);
    }
}
