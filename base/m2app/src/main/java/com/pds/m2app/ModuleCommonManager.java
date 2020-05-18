package com.pds.m2app;

import io.github.prototypez.appjoint.AppJoint;

/**
 * 跨module调用管理类
 *
 * @author hmy
 */
public class ModuleCommonManager {

    private static ModuleCommonManager sInstance = new ModuleCommonManager();
    private ModuleCommonService mModuleCommonService;

    public static ModuleCommonManager getInstance() {
        return sInstance;
    }

    private ModuleCommonManager() {
    }

    /***/
    public synchronized ModuleCommonService getModuleService() {
        if (mModuleCommonService == null) {
            mModuleCommonService = AppJoint.service(ModuleCommonService.class);
        }
        return mModuleCommonService;
    }
}
