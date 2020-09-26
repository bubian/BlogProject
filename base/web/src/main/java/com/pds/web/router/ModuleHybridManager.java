package com.pds.web.router;

import com.alibaba.android.arouter.launcher.ARouter;

public class ModuleHybridManager {

    public static final String ROUTE_HYBRID = "/link/hybrid";
    private volatile static ModuleHybridManager sInstance;
    private ModuleHybridService mHybridService;

    public static ModuleHybridManager getInstance() {
        if (sInstance == null) {
            synchronized (ModuleHybridManager.class) {
                if (sInstance == null) {
                    sInstance = new ModuleHybridManager();
                }
            }
        }
        return sInstance;
    }

    public ModuleHybridService getService() {
        if (null == mHybridService) {
            mHybridService = ARouter.getInstance().navigation(ModuleHybridService.class);
        }
        return mHybridService;
    }
}
