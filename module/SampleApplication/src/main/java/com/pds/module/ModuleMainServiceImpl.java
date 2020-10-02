package com.pds.module;


import android.app.Application;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.application.BaseApplication;
import com.pds.sample.module.ModuleMainService;


/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/23 7:44 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = "/service/sample")
public class ModuleMainServiceImpl implements ModuleMainService {

    @Override
    public Application getApplication() {
        return BaseApplication.getApplication();
    }
}
