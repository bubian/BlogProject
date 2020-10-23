package com.pds.sample.application;

import android.app.Application;

import com.pds.base.manager.ActivityLifecycle;
import com.pds.router.ModuleRouter;
import com.pds.skin.SkinManager;
import com.pds.tool.ToolApplicationManager;
import com.pds.web.X5SDK;

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
        ModuleRouter.init(application);
        SkinManager.init(mApplication);
        X5SDK.init(application);
        ToolApplicationManager.onCreate(application);
        application.registerActivityLifecycleCallbacks(ActivityLifecycle.getInstance());
    }

    public Application application(){
        return mApplication;
    }
}
