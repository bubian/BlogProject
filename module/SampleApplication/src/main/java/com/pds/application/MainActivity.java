package com.pds.application;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.BundleKey;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.router.ARouterPath;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/16 4:47 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.jump).setOnClickListener(v -> ARouterHelper.nav(this,SampleGroupRouter.FILE_LOAD));
    }
}
