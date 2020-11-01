package com.pds.flutter;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.flutter.view.FlutterMain;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:07 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 在这里初始化相关sdk
 */
public class ModuleFlutter extends MultiDexApplication {
    private Application mApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static void init(Application application) {
        instance().mApplication = application;
        FlutterMain.startInitialization(application);
    }

    public static final ModuleFlutter instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final ModuleFlutter INSTANCE = new ModuleFlutter();
    }

    public Application appContext() {
        if (null == mApplication) {
            mApplication = findApplicationFromApp();
        }

        if (null == mApplication) {
            mApplication = findApplicationFromSystem();
        }

        if (null == mApplication) {
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
        } catch (Exception ignored) {
        }
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
        } catch (Exception ignored) {
        }
        return null;
    }
}
