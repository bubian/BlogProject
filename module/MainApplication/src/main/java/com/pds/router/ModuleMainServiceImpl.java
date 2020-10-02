package com.pds.router;


import android.app.Application;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.application.BaseApplication;

import java.pds.main.router.ModuleMainService;


/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/23 7:44 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = "/service/main")
public class ModuleMainServiceImpl implements ModuleMainService {

    @Override
    public Application getApplication() {
        return BaseApplication.getApplication();
    }
}
