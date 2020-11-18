package com.pds.splugin.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.pds.splugin.model.InterfaceService;
import com.pds.splugin.manager.PluginManager;

import java.lang.reflect.Constructor;

public class ProxyService extends  Service{
    String serviceName;
    InterfaceService payInterfaceService;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return null;
    }

    private void init(Intent intent) {
        serviceName = intent.getStringExtra("serviceName");
        try {
            Class loadClass= PluginManager.getInstance().getDexClassLoader().loadClass(serviceName);

            Constructor<?> localConstructor =loadClass.getConstructor(new Class[] {});
            Object instance = localConstructor.newInstance(new Object[] {});
            payInterfaceService = (InterfaceService) instance;
            payInterfaceService.attach(this);
            Bundle bundle = new Bundle();
            bundle.putInt("form", 1);
            payInterfaceService.onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (payInterfaceService == null) {
            init(intent);
        }

        return payInterfaceService.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        payInterfaceService.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        payInterfaceService.onLowMemory();
        super.onLowMemory();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        payInterfaceService.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        payInterfaceService.onRebind(intent);
        super.onRebind(intent);
    }

}
