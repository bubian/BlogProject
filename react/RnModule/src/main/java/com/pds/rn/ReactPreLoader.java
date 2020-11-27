package com.pds.rn;

import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactRootView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/27 4:27 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ReactPreLoader {
    private static boolean mIsPreLoaded = false;

    public static void preLoad(Context context, String componentName) {
        if (mIsPreLoaded) {
            return;
        }
        mIsPreLoaded = true;
        ReactRootView reactRootView = new ReactRootView(context);
        reactRootView.startReactApplication(((ReactApplication) context).getReactNativeHost().getReactInstanceManager(), componentName, null);
    }
}
