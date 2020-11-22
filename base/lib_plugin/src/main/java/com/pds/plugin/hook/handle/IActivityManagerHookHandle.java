/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.pds.plugin.hook.handle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.RemoteException;

import com.pds.plugin.am.RunningActivities;
import com.pds.plugin.core.PluginProcessManager;
import com.pds.plugin.hook.BaseClassHandle;
import com.pds.plugin.hook.BaseMethodHandle;
import com.pds.plugin.pm.PluginManager;

import java.lang.reflect.Method;

public class IActivityManagerHookHandle extends BaseClassHandle {

    private static final String TAG = IActivityManagerHookHandle.class.getSimpleName();

    public IActivityManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("startActivity", new StartActivity(mHostContext));
        // 不可或缺
        sHookedMethodHandlers.put("registerReceiver", new registerReceiver(mHostContext));

    }

    private static ComponentName selectProxyActivity(Intent intent) {
        try {
            if (intent != null) {
                ActivityInfo proxyInfo = PluginManager.getInstance().selectStubActivityInfo(intent);
                if (proxyInfo != null) {
                    return new ComponentName(proxyInfo.packageName, proxyInfo.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static ActivityInfo resolveActivity(Intent intent) throws RemoteException {
        return PluginManager.getInstance().resolveActivityInfo(intent, 0);
    }
    private static class StartActivity extends BaseMethodHandle {

        public StartActivity(Context hostContext) {
            super(hostContext);
        }
        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            RunningActivities.beforeStartActivity();
            Intent intent=null;
            int index= findFirstIntentIndexInArgs(args);
            if (args != null && args.length > 1 && index >= 0) {
                intent = (Intent) args[index];
            }
//            插件的Intent   转换成插件的 ActivityInfo
            ActivityInfo activityInfo = resolveActivity(intent);
//            将插件的activityInfo  转换成    预注册进程的 ComponentName
            if(activityInfo!=null && PluginManager.getInstance().isPluginPackage(activityInfo.packageName)){
                ComponentName componentName=selectProxyActivity(intent);

                Intent newIntent = new Intent();
                ClassLoader pluginClassLoader = PluginProcessManager.getPluginClassLoader(componentName.getPackageName());
                newIntent.setComponent(componentName);
//                真实的意图 被我隐藏到了  键值对
                newIntent.putExtra("oldIntent", intent);
                args[index] = newIntent;
                args[1] = mHostContext.getPackageName();
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }


    private static class registerReceiver extends BaseMethodHandle {

        public registerReceiver(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (args != null && args.length > 0) {
                    for (int index = 0; index < args.length; index++) {
                        if (args[index] instanceof String) {
                            String callerPackage = (String) args[index];
                            if (isPackagePlugin(callerPackage)) {
                                args[index] = mHostContext.getPackageName();
                            }
                        }
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
    private static boolean isPackagePlugin(String packageName) throws RemoteException {
        return PluginManager.getInstance().isPluginPackage(packageName);
    }
    private static int findFirstIntentIndexInArgs(Object[] args) {
        if (args != null && args.length > 0) {
            int i = 0;
            for (Object arg : args) {
                if (arg != null && arg instanceof Intent) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

}
