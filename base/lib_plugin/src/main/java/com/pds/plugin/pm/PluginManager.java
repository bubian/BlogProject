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

package com.pds.plugin.pm;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.pds.plugin.BuildConfig;
import com.pds.plugin.PluginManagerService;
import com.pds.plugin.helper.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 插件包管理服务的客户端实现。
 * <p/>
 */
public class PluginManager implements ServiceConnection {

    public static final String ACTION_PACKAGE_ADDED = "com.pds.plugin.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "com.pds.plugin.PACKAGE_REMOVED";
    public static final String ACTION_DROIDPLUGIN_INIT = "com.pds.plugin.ACTION_DROIDPLUGIN_INIT";
    public static final String ACTION_MAINACTIVITY_ONCREATE = "com.pds.plugin.ACTION_MAINACTIVITY_ONCREATE";
    public static final String ACTION_MAINACTIVITY_ONDESTORY = "com.pds.plugin.ACTION_MAINACTIVITY_ONDESTORY";
    public static final String ACTION_SETTING = "com.pds.plugin.ACTION_SETTING";
    public static final String ACTION_SHORTCUT_PROXY = "com.pds.plugin.ACTION_SHORTCUT_PROXY";


    public static final String EXTRA_PID = "com.pds.plugin.EXTRA_PID";
    public static final String EXTRA_PACKAGENAME = "com.pds.plugin.EXTRA_EXTRA_PACKAGENAME";

    public static final String STUB_AUTHORITY_NAME = BuildConfig.AUTHORITY_NAME;
    public static final String EXTRA_APP_PERSISTENT = "com.pds.plugin.EXTRA_APP_PERSISTENT";


    public static final int INSTALL_FAILED_NO_REQUESTEDPERMISSION = -100001;
    public static final int STUB_NO_ACTIVITY_MAX_NUM = 4;


    private static final String TAG = PluginManager.class.getSimpleName();


    private Context mHostContext;
    private static PluginManager sInstance = null;

    private List<WeakReference<ServiceConnection>> sServiceConnection = Collections.synchronizedList(new ArrayList<WeakReference<ServiceConnection>>(1));

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        mPluginManager = IPluginManager.Stub.asInterface(iBinder);
        new Thread() {
            @Override
            public void run() {
                try {
                    mPluginManager.waitForReady();

                    Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
                    while (iterator.hasNext()) {
                        WeakReference<ServiceConnection> wsc = iterator.next();
                        ServiceConnection sc = wsc != null ? wsc.get() : null;
                        if (sc != null) {
                            sc.onServiceConnected(componentName, iBinder);
                        } else {
                            iterator.remove();
                        }
                    }

                    mPluginManager.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                        @Override
                        public void binderDied() {
                            onServiceDisconnected(componentName);
                        }
                    }, 0);

                    Log.i(TAG, "PluginManager ready!");
                } catch (Throwable e) {
                    Log.e(TAG, "Lost the mPluginManager connect...", e);
                } finally {
                    try {
                        synchronized (mWaitLock) {
                            mWaitLock.notifyAll();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "PluginManager notifyAll:" + e.getMessage());
                    }
                }
            }
        }.start();
        Log.i(TAG, "onServiceConnected connected OK!");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(TAG, "onServiceDisconnected disconnected!");
        mPluginManager = null;

        Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
        while (iterator.hasNext()) {
            WeakReference<ServiceConnection> wsc = iterator.next();
            ServiceConnection sc = wsc != null ? wsc.get() : null;
            if (sc != null) {
                sc.onServiceDisconnected(componentName);
            } else {
                iterator.remove();
            }
        }
        //服务连接断开，需要重新连接。
        connectToService();
    }

    private Object mWaitLock = new Object();

    public void waitForConnected() {
        if (isConnected()) {
            return;
        } else {
            try {
                synchronized (mWaitLock) {
                    mWaitLock.wait();
                }
            } catch (InterruptedException e) {
                Log.i(TAG, "waitForConnected:" + e.getMessage());
            }
            Log.i(TAG, "waitForConnected finish");
        }
    }


    /**
     * 提供超时设置的waitForConnected版本
     *
     * @param timeout，当超时时间大于0时超时设置生效
     */
    public void waitForConnected(long timeout) {
        if (timeout > 0) {
            if (isConnected()) {
                return;
            } else {
                try {
                    synchronized (mWaitLock) {
                        mWaitLock.wait(timeout);
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, "waitForConnected:" + e.getMessage());
                }
                Log.i(TAG, "waitForConnected finish");
            }
        } else {
            waitForConnected();
        }
    }


    private IPluginManager mPluginManager;

    public void connectToService() {
        if (mPluginManager == null) {
            try {
                Intent intent = new Intent(mHostContext, PluginManagerService.class);
                intent.setPackage(mHostContext.getPackageName());
                mHostContext.startService(intent);
                mHostContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                Log.e(TAG, "connectToService", e);
            }

        }
    }

    public void addServiceConnection(ServiceConnection sc) {
        sServiceConnection.add(new WeakReference<ServiceConnection>(sc));
    }

    public void removeServiceConnection(ServiceConnection sc) {
        Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
        while (iterator.hasNext()) {
            WeakReference<ServiceConnection> wsc = iterator.next();
            if (wsc.get() == sc) {
                iterator.remove();
            }
        }
    }


    public void init(Context hostContext) {
        mHostContext = hostContext;
        connectToService();
    }

    public Context getHostContext() {
        return mHostContext;
    }

    public boolean isConnected() {
        return mHostContext != null && mPluginManager != null;
    }

    public static PluginManager getInstance() {
        if (sInstance == null) {
            sInstance = new PluginManager();
        }
        return sInstance;
    }

    //////////////////////////
    //  API
    //////////////////////////
    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getPackageInfo(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getPackageInfo", e);
        }
        return null;
    }


    public boolean isPluginPackage(String packageName) throws RemoteException {
        try {
            if (mHostContext == null) {
                return false;
            }
            if (TextUtils.equals(mHostContext.getPackageName(), packageName)) {
                return false;
            }

            if (mPluginManager != null && packageName != null) {
                return mPluginManager.isPluginPackage(packageName);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "isPluginPackage", e);
        }
        return false;
    }

    public boolean isPluginPackage(ComponentName className) throws RemoteException {
        if (className == null) {
            return false;
        }
        return isPluginPackage(className.getPackageName());
    }

    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws NameNotFoundException, RemoteException {

        try {
            if (className == null) {
                return null;
            }
            if (mPluginManager != null && className != null) {
                return mPluginManager.getActivityInfo(className, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getActivityInfo RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getActivityInfo", e);
        }
        return null;
    }



    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.resolveIntent(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "resolveIntent", e);
        }
        return null;
    }



    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.queryIntentActivities(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "queryIntentActivities RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "queryIntentActivities", e);
        }
        return null;
    }


    public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getInstalledPackages(flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getInstalledPackages RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getInstalledPackages", e);
        }
        return null;
    }




    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && packageName != null) {
                return mPluginManager.getApplicationInfo(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getApplicationInfo RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getApplicationInfo", e);
        }
        return null;
    }

    public ActivityInfo selectStubActivityInfo(ActivityInfo pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubActivityInfo(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }

    public ActivityInfo selectStubActivityInfo(Intent pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubActivityInfoByIntent(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }


    public ActivityInfo resolveActivityInfo(Intent intent, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                if (intent.getComponent() != null) {
                    return mPluginManager.getActivityInfo(intent.getComponent(), flags);
                } else {
                    ResolveInfo resolveInfo = mPluginManager.resolveIntent(intent, intent.resolveTypeIfNeeded(mHostContext.getContentResolver()), flags);
                    if (resolveInfo != null && resolveInfo.activityInfo != null) {
                        return resolveInfo.activityInfo;
                    }
                }
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
            return null;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }

    public List<ActivityInfo> getReceivers(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getReceivers(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getReceivers", e);
        }
        return null;
    }

    public List<IntentFilter> getReceiverIntentFilter(ActivityInfo info) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getReceiverIntentFilter(info);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getReceiverIntentFilter", e);
        }
        return null;
    }

    public int installPackage(String filepath, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                int result = mPluginManager.installPackage(filepath, flags);
                Log.w(TAG, String.format("%s install result %d", filepath, result));
                return result;
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }
        return -1;
    }

    public String getProcessNameByPid(int pid) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getProcessNameByPid(pid);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }
        return null;
    }

    public void onActivityCreated(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivityCreated(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onActivityCreated", e);
        }
    }

    public void onActivityDestory(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivityDestory(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onActivityDestroy", e);
        }
    }



    public void reportMyProcessName(String stubProcessName, String targetProcessName, String targetPkg) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.reportMyProcessName(stubProcessName, targetProcessName, targetPkg);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "reportMyProcessName", e);
        }
    }

    public void deletePackage(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.deletePackage(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "deletePackage", e);
        }
    }
}
