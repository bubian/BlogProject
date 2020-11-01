package com.pds.main;

import android.app.Application;
import android.content.Context;

import com.pds.flutter.ModuleFlutter;
import com.pds.kotlin.study.ModuleKotlin;
import com.pds.rn.ModuleRn;
import com.pds.router.ModuleRouter;
import com.pds.sample.application.ModuleSample;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:31 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleApplication {

    private static final String TAG = "MainApplication";
    private static ModuleApplication sModuleApplication = new ModuleApplication();
    private Application mApplication;

    private ModuleApplication() {
    }

    public static ModuleApplication instance() {
        return sModuleApplication;
    }

    public void onCreate(Application application) {
        mApplication = application;
        ModuleRn.init(application);
        ModuleFlutter.init(application);
        ModuleSample.instance().onCreate(application);
        ModuleRouter.init(application);
        ModuleKotlin.instance().init(application);
    }

    public void attachBaseContext(Context base) {
        ModuleSample.instance().attachBaseContext(base);
    }
}
