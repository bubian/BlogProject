package com.pds.rn;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.medrn.codepush.RNCodePush;
import com.pds.rn.pa.CommonReactPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author pengdaosong
 */

public class ModuleRn implements ReactApplication {

    private Application mApplication;
    /******************************Rn配置***************************************/

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(appContext()) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new CommonReactPackage()
                    // new RNGestureHandlerPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }

        @javax.annotation.Nullable
        @Override
        protected String getJSBundleFile() {
            return super.getJSBundleFile();
            // try {
                //加载rn页面时，才会执行
                // return CodePush.getJSBundleFile(Constants.JS_BUNDLE_FILE_NAME);
                // return RNCodePush.getJSBundleFile();
            // } catch (Exception e) {
                // return null;
            // }
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }



    public static void init(Application application) {
        instance().mApplication = application;
        SoLoader.init(application, false);
        // initRn(application);
    }

    private static String getRNCodePushServerUrl(boolean isDebug) {
        return isDebug ? "http://172.12.34.50" : "http://172.12.34.53";
    }
    private static void initRn(Application application){
        RNCodePush.init(application, BuildConfig.DEBUG, getRNCodePushServerUrl(BuildConfig.DEBUG), (o, arg) -> {
            try {
                ReactPreLoader.preLoad(application, "FakeApp");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkRnUpdate(){
        RNCodePush.getInstance().checkReload();
        RNCodePush.getInstance().checkUpdate(appContext(), BuildConfig.DEBUG);
    }

    public static final ModuleRn instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final ModuleRn INSTANCE = new ModuleRn();
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
