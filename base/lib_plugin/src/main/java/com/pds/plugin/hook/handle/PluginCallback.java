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
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.pds.plugin.core.Env;
import com.pds.plugin.core.PluginProcessManager;
import com.pds.plugin.pm.PluginManager;
import com.pds.plugin.reflect.FieldUtils;
import com.pds.plugin.helper.Log;


public class PluginCallback implements Handler.Callback {

    private static final String TAG = "PluginCallback";

    public static final int LAUNCH_ACTIVITY = 100;
    private Handler mOldHandle = null;
    private Handler.Callback mCallback = null;
    private Context mHostContext;

    private boolean mEnable = false;

    public PluginCallback(Context hostContext, Handler oldHandle, Handler.Callback callback) {
        mOldHandle = oldHandle;
        mCallback = callback;
        mHostContext = hostContext;
    }

    public void setEnable(boolean enable) {
        this.mEnable = enable;
    }

    public boolean isEnable() {
        return mEnable;
    }

    @Override
    public boolean handleMessage(Message msg) {
        long b = System.currentTimeMillis();
        try {
            if (!mEnable) {
                return false;
            }

            if (PluginProcessManager.isPluginProcess(mHostContext)) {
                if (!PluginManager.getInstance().isConnected()) {
                    //这里必须要这么做。如果当前进程是插件进程，并且，还没有绑定上插件管理服务，我们则把消息延迟一段时间再处理。
                    //这样虽然会降低启动速度，但是可以解决在没绑定服务就启动，会导致的一系列时序问题。
                    Log.i(TAG, "handleMessage not isConnected post and wait,msg=%s", msg);
                    mOldHandle.sendMessageDelayed(Message.obtain(msg), 5);
                    //返回true，告诉下面的handle不要处理了。
                    return true;
                }
            }

            if (msg.what == LAUNCH_ACTIVITY) {
                return handleLaunchActivity(msg);
            }
            if (mCallback != null) {
                return mCallback.handleMessage(msg);
            } else {
                return false;
            }
        } finally {

        }
    }
    private boolean handleLaunchActivity(Message msg) {
        try {
            Object obj = msg.obj;
            Intent stubIntent = (Intent) FieldUtils.readField(obj, "intent");
            //ActivityInfo activityInfo = (ActivityInfo) FieldUtils.readField(obj, "activityInfo", true);
            stubIntent.setExtrasClassLoader(mHostContext.getClassLoader());
            Intent targetIntent = stubIntent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
            // 这里多加一个isNotShortcutProxyActivity的判断，因为ShortcutProxyActivity的很特殊，启动它的时候，
            // 也会带上一个EXTRA_TARGET_INTENT的数据，就会导致这里误以为是启动插件Activity，所以这里要先做一个判断。
            // 之前ShortcutProxyActivity错误复用了key，但是为了兼容，所以这里就先这么判断吧。
            if (targetIntent != null ) {
//                IPackageManagerHook.fixContextPackageManager(mHostContext);
                ComponentName targetComponentName = targetIntent.resolveActivity(mHostContext.getPackageManager());
                ActivityInfo targetActivityInfo = PluginManager.getInstance().getActivityInfo(targetComponentName, 0);
                if (targetActivityInfo != null) {

                    if (targetComponentName != null && targetComponentName.getClassName().startsWith(".")) {
                        targetIntent.setClassName(targetComponentName.getPackageName(), targetComponentName.getPackageName() + targetComponentName.getClassName());
                    }

                    ResolveInfo resolveInfo = mHostContext.getPackageManager().resolveActivity(stubIntent, 0);
                    ActivityInfo stubActivityInfo = resolveInfo != null ? resolveInfo.activityInfo : null;
                    if (stubActivityInfo != null) {
                        PluginManager.getInstance().reportMyProcessName(stubActivityInfo.processName, targetActivityInfo.processName, targetActivityInfo.packageName);
                    }
                    PluginProcessManager.preLoadApk(mHostContext, targetActivityInfo);
//                    ClassLoader pluginClassLoader = PluginProcessManager.getPluginClassLoader(targetComponentName.getPackageName());
//                    setIntentClassLoader(targetIntent, pluginClassLoader);
//                    setIntentClassLoader(stubIntent, pluginClassLoader);

                    boolean success = false;
                    try {
                        targetIntent.putExtra(Env.EXTRA_TARGET_INFO, targetActivityInfo);
                        if (stubActivityInfo != null) {
                            targetIntent.putExtra(Env.EXTRA_STUB_INFO, stubActivityInfo);
                        }
                        success = true;
                    } catch (Exception e) {
                        Log.e(TAG, "putExtra 1 fail", e);
                    }
                    if (!success) {
                        Intent newTargetIntent = new Intent();
                        newTargetIntent.setComponent(targetIntent.getComponent());
                        newTargetIntent.putExtra(Env.EXTRA_TARGET_INFO, targetActivityInfo);
                        if (stubActivityInfo != null) {
                            newTargetIntent.putExtra(Env.EXTRA_STUB_INFO, stubActivityInfo);
                        }
                        FieldUtils.writeDeclaredField(msg.obj, "intent", newTargetIntent);
                    } else {
                        FieldUtils.writeDeclaredField(msg.obj, "intent", targetIntent);
                    }
                    FieldUtils.writeDeclaredField(msg.obj, "activityInfo", targetActivityInfo);

                    Log.i(TAG, "handleLaunchActivity OK");
                } else {
                    Log.e(TAG, "handleLaunchActivity oldInfo==null");
                }
            } else {
                Log.e(TAG, "handleLaunchActivity targetIntent==null");
            }
        } catch (Exception e) {
            Log.e(TAG, "handleLaunchActivity FAIL", e);
        }

        if (mCallback != null) {
            return mCallback.handleMessage(msg);
        } else {
            return false;
        }
    }


    private ClassLoader fixedClassLoader(ClassLoader newParent) {
        ClassLoader nowClassLoader = PluginCallback.class.getClassLoader();
        ClassLoader oldParent = nowClassLoader.getParent();
        try {
            if (newParent != null && newParent != oldParent) {
                FieldUtils.writeField(nowClassLoader, "parent", newParent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldParent;
    }

    private void setIntentClassLoader(Intent intent, ClassLoader classLoader) {
        try {
            Bundle mExtras = (Bundle) FieldUtils.readField(intent, "mExtras");
            if (mExtras != null) {
                mExtras.setClassLoader(classLoader);
            } else {
                Bundle value = new Bundle();
                value.setClassLoader(classLoader);
                FieldUtils.writeField(intent, "mExtras", value);
            }
        } catch (Exception e) {
        } finally {
            intent.setExtrasClassLoader(classLoader);
        }
    }

}