package com.pds.splugin.proxy;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.router.module.ModuleGroupRouter;

@Route(path = ModuleGroupRouter.PLUGIN_ACTIVITY_REPLACE)
public class ProxyReplaceActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
