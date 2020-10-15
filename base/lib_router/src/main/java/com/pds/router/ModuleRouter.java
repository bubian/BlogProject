package com.pds.router;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 11:58 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleRouter {
    private Application mApplication;

    /**
     * 不一定要在启动的时候初始化，根据业务而定
     *
     * @param application
     */
    public static void init(Application application) {
        instance().mApplication = application;
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            // 使用自己的日志工具打印日志
            // ARouter.setLogger();
            // 使用自己提供的线程池
            // ARouter.setExecutor();
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
    }

    public static final ModuleRouter instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final ModuleRouter INSTANCE = new ModuleRouter();
    }

    public Application appContext() {
        if (null == mApplication) {
            mApplication = findApplicationFromApp();
        }

        if (null == mApplication) {
            mApplication = findApplicationFromSystem();
        }

        if (null == mApplication) {
            throw new NullPointerException("ModuleRouter appContext is null");
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

    public void destroy(){
        ARouter.getInstance().destroy();
    }
}
