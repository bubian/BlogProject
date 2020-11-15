package com.pds.splugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.splugin.common.InterfaceActivity;

import java.lang.reflect.Constructor;

@Route(path = ModuleGroupRouter.PLUGIN_PHONE_PROXY)
public class ProxyActivity  extends Activity {
    private String className;
    InterfaceActivity payInterfaceActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState  ) {
        // 设置主题
        setTheme(android.R.style.Theme_Light_NoTitleBar);
        // setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState );
        className = getIntent().getStringExtra("className");
        try {
            Class activityClass = getClassLoader().loadClass(className);
            Constructor constructor = activityClass.getConstructor(new Class[]{});
            Object instance= constructor.newInstance(new Object[]{});
            payInterfaceActivity = (InterfaceActivity) instance;
            payInterfaceActivity.attach(this);
            Bundle bundle = new Bundle();
            payInterfaceActivity.onCreate(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        String className1=intent.getStringExtra("className");
        Intent intent1 = new Intent(this, ProxyActivity.class);
        intent1.putExtra("className", className1);
        super.startActivity(intent1);
    }

    @Override
    public ComponentName startService(Intent service) {
        String serviceName = service.getStringExtra("serviceName");
        Intent intent1 = new Intent(this, ProxyService.class);
        intent1.putExtra("serviceName", serviceName);
        return super.startService(intent1);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        payInterfaceActivity.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        payInterfaceActivity.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payInterfaceActivity.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        payInterfaceActivity.onPause();
    }
}
