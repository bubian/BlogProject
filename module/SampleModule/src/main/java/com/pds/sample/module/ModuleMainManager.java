package com.pds.sample.module;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author hmy
 * @time 2020/9/18 12:40
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
