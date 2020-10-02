package com.pds.sample.application;

import android.app.Application;

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

    private ModuleApplication(){}

    public static ModuleApplication instance(){
        return sModuleApplication;
    }

    public void onCreate(Application application) {
        mApplication = application;
    }
}
