package com.pds.blog.web;

import android.content.Context;
import android.util.Log;

import com.pds.blog.web.core.HbCookieSync;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 11:12
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class X5Manager {

    private static final String TAG = "X5Manager";
    public static void init(Context context){
        HbCookieSync.initCookie(context);
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
}
