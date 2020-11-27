package com.medrn.codepush.util;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.JSBundleLoader;
import com.medrn.codepush.RNCodePush;

import java.lang.reflect.Field;

public class RNReloadUtil {

    private Application mApplication;

    public static RNReloadUtil getInstance() {
        return Singleton.sInstance;
    }

    private static class Singleton {
        private static final RNReloadUtil sInstance = new RNReloadUtil();
    }

    public void reloadBundle(Application application) {
        mApplication = application;
        if (null == mApplication) {
            return;
        }
        final ReactInstanceManager instanceManager;
        try {
            instanceManager = resolveInstanceManager();
            if (instanceManager == null) {
                return;
            }
            String latestJSBundleFile = RNCodePush.getJSBundleFile();
            setJSBundle(instanceManager, latestJSBundleFile);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        instanceManager.recreateReactContextInBackground();
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private ReactInstanceManager resolveInstanceManager() {
        ReactInstanceManager instanceManager;
        instanceManager = ((ReactApplication) mApplication).getReactNativeHost().getReactInstanceManager();
        return instanceManager;
    }

    private void setJSBundle(ReactInstanceManager instanceManager, String latestJSBundleFile) throws IllegalAccessException {
        try {
            JSBundleLoader latestJSBundleLoader;
            if (latestJSBundleFile.toLowerCase().startsWith("assets://")) {
                latestJSBundleLoader = JSBundleLoader.createAssetLoader(mApplication, latestJSBundleFile, false);
            } else {
                latestJSBundleLoader = JSBundleLoader.createFileLoader(latestJSBundleFile);
            }
            Field bundleLoaderField = instanceManager.getClass().getDeclaredField("mBundleLoader");
            bundleLoaderField.setAccessible(true);
            bundleLoaderField.set(instanceManager, latestJSBundleLoader);
            bundleLoaderField.setAccessible(false);
        } catch (Exception e) {
            throw new IllegalAccessException("Could not setJSBundle");
        }
    }
}
