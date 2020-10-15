package com.pds.sample.router;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * 也可以直接用下面的方式调用，详情参考官网
 * ((HelloService) ARouter.getInstance().build("/service/sample").navigation()).getApplication();
 */
public class ModuleMainManager {

    private volatile static ModuleMainManager sInstance;
    private ModuleMainService mModuleMainService;

    public static ModuleMainManager getInstance() {
        if (sInstance == null) {
            synchronized (ModuleMainManager.class) {
                if (sInstance == null) {
                    sInstance = new ModuleMainManager();
                }
            }
        }
        return sInstance;
    }

    public ModuleMainService getService() {
        if (null == mModuleMainService) {
            mModuleMainService = ARouter.getInstance().navigation(ModuleMainService.class);
        }
        return mModuleMainService;
    }
}
