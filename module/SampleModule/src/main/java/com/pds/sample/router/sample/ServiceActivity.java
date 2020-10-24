package com.pds.sample.router.sample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.core.ARouterServiceHelper;
import com.pds.router.core.service.SingleService;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:06 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = ARouterConstants.SERVICE_ACTIVITY)
public class ServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouterServiceHelper.<SingleService>nav(ARouterConstants.SINGLE).sayHello("测试");
        Fragment fragment = (Fragment) ARouter.getInstance().build(ARouterConstants.AR_FRAGMENT).navigation();
        // 获取原始的URI
        String uriStr = getIntent().getStringExtra(ARouter.RAW_URI);
    }
}
