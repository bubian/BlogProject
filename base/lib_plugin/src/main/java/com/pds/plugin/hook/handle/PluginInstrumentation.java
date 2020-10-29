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

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.pds.plugin.am.RunningActivities;
import com.pds.plugin.core.Env;
import com.pds.plugin.core.PluginProcessManager;
import com.pds.plugin.hook.proxy.IPackageManagerHook;
import com.pds.plugin.pm.PluginManager;
import com.pds.plugin.reflect.FieldUtils;
import com.pds.plugin.helper.Log;

import java.lang.reflect.Field;

public class PluginInstrumentation extends Instrumentation {

    protected static final String TAG = PluginInstrumentation.class.getSimpleName();

    protected Instrumentation mTarget;
    private final Context mHostContext;
    private boolean enable = true;

    public void setEnable(boolean enable) {

        this.enable = enable;
        this.enable = true;
    }

    public PluginInstrumentation(Context hostContext, Instrumentation target) {
        mTarget = target;
        mHostContext = hostContext;
    }
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        android.util.Log.i(TAG, "callActivityOnCreate: ");
        if (enable) {
            IPackageManagerHook.fixContextPackageManager(activity);
            try {
                onActivityCreated(activity);
            } catch (RemoteException e) {
                Log.e(TAG, "callActivityOnCreate:onActivityCreated", e);
            }

            try {
                fixBaseContextImplOpsPackage(activity.getBaseContext());
            } catch (Exception e) {
            }

            try {
                fixBaseContextImplContentResolverOpsPackage(activity.getBaseContext());
            } catch (Exception e) {
                Log.e(TAG, "callActivityOnCreate:fixBaseContextImplContentResolverOpsPackage", e);
            }


        }
        super.callActivityOnCreate(activity, icicle);
    }

    private void fixBaseContextImplOpsPackage(Context context) throws IllegalAccessException {
        android.util.Log.i(TAG, "fixBaseContextImplOpsPackage: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && context != null && !TextUtils.equals(context.getPackageName(), mHostContext.getPackageName())) {
            Context baseContext = context;
            Class clazz = baseContext.getClass();
            Field mOpPackageName = FieldUtils.getDeclaredField(clazz, "mOpPackageName", true);
            if (mOpPackageName != null) {
                Object valueObj = mOpPackageName.get(baseContext);
                if (valueObj instanceof String) {
                    String opPackageName = ((String) valueObj);
                    if (!TextUtils.equals(opPackageName, mHostContext.getPackageName())) {
                        mOpPackageName.set(baseContext, mHostContext.getPackageName());
                        Log.i(TAG, "fixBaseContextImplOpsPackage OK!Context=%s,", baseContext);
                    }
                }
            }
        }
    }

    private void fixBaseContextImplContentResolverOpsPackage(Context context) throws IllegalAccessException {
        android.util.Log.i(TAG, "fixBaseContextImplContentResolverOpsPackage: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && context != null && !TextUtils.equals(context.getPackageName(), mHostContext.getPackageName())) {
            Context baseContext = context;
            Class clazz = baseContext.getClass();
            Field mContentResolver = FieldUtils.getDeclaredField(clazz, "mContentResolver", true);
            if (mContentResolver != null) {
                Object valueObj = mContentResolver.get(baseContext);
                if (valueObj instanceof ContentResolver) {
                    ContentResolver contentResolver = ((ContentResolver) valueObj);
                    Field mPackageName = FieldUtils.getDeclaredField(ContentResolver.class, "mPackageName", true);
                    Object mPackageNameValueObj = mPackageName.get(contentResolver);
                    if (mPackageNameValueObj != null && mPackageNameValueObj instanceof String) {
                        String packageName = ((String) mPackageNameValueObj);
                        if (!TextUtils.equals(packageName, mHostContext.getPackageName())) {
                            mPackageName.set(contentResolver, mHostContext.getPackageName());
                            Log.i(TAG, "fixBaseContextImplContentResolverOpsPackage OK!Context=%s,contentResolver=%s", baseContext, contentResolver);
                        }
                    }

                }
            }
        }
    }


    private void onActivityCreated(Activity activity) throws RemoteException {
        android.util.Log.i(TAG, "onActivityCreated: ");
        try {
            Intent targetIntent = activity.getIntent();
            if (targetIntent != null) {
                ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
                ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
                if (targetInfo != null && stubInfo != null) {
                    RunningActivities.onActivtyCreate(activity, targetInfo, stubInfo);
                    PluginManager.getInstance().onActivityCreated(stubInfo, targetInfo);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "onActivityCreated fail", e);
        }
    }



    private void onActivityDestory(Activity activity) throws RemoteException {
        android.util.Log.i(TAG, "onActivityDestory: ");
        Intent targetIntent = activity.getIntent();
        if (targetIntent != null) {
            ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
            ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
            if (targetInfo != null && stubInfo != null) {
                PluginManager.getInstance().onActivityDestory(stubInfo, targetInfo);
            }
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        android.util.Log.i(TAG, "callActivityOnDestroy: ");
        if (mTarget != null) {
            mTarget.callActivityOnDestroy(activity);
        } else {
            super.callActivityOnDestroy(activity);
        }
        RunningActivities.onActivtyDestory(activity);

        if (enable) {
            try {
                onActivityDestory(activity);
            } catch (RemoteException e) {
                Log.e(TAG, "callActivityOnDestroy:onActivityDestroy", e);
            }
        }
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        android.util.Log.i(TAG, "callApplicationOnCreate: ");
        if (enable) {
            IPackageManagerHook.fixContextPackageManager(app);
            try {
//                PluginProcessManager.fakeSystemService(mHostContext, app);
            } catch (Exception e) {
                Log.e(TAG, "fakeSystemService", e);
            }

            try {
                fixBaseContextImplOpsPackage(app.getBaseContext());
            } catch (Exception e) {
                Log.e(TAG, "callApplicationOnCreate:fixBaseContextImplOpsPackage", e);
            }

            try {
                fixBaseContextImplContentResolverOpsPackage(app.getBaseContext());
            } catch (Exception e) {
                Log.e(TAG, "callActivityOnCreate:fixBaseContextImplContentResolverOpsPackage", e);
            }
        }


        if (mTarget != null) {
            mTarget.callApplicationOnCreate(app);
        } else {
            super.callApplicationOnCreate(app);
        }

        if (enable) {
            try {
                PluginProcessManager.registerStaticReceiver(app, app.getApplicationInfo(), app.getClassLoader());
            } catch (Exception e) {
                Log.e(TAG, "registerStaticReceiver", e);
            }
        }
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        android.util.Log.i(TAG, "callActivityOnNewIntent: ");
        try {
            Intent newIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
            if (newIntent != null) {
                intent = newIntent;
            }
        } catch (Throwable e) {
            Log.e(TAG, "callActivityOnNewIntent:read EXTRA_TARGET_INTENT", e);
        }
        super.callActivityOnNewIntent(activity, intent);
    }


}
