package com.pds.tools;

import android.app.Application;
import android.util.Log;

import com.pds.tools.core.dokit.DoKitInit;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/2 9:38 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleTools {

    private static final String TAG = "ModuleInit";

    public static void init(Application application) {
        Log.d(TAG, "init");
        DoKitInit.init(application,DoKitInit.APP_ID);
    }
}
