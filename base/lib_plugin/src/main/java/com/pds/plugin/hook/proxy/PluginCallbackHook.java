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

package com.pds.plugin.hook.proxy;

import android.content.Context;
import android.os.Handler;

import com.pds.plugin.hook.BaseClassHandle;
import com.pds.plugin.hook.Hook;
import com.pds.plugin.hook.handle.PluginCallback;
import com.pds.plugin.reflect.FieldUtils;
import com.pds.plugin.helper.Log;
import com.pds.plugin.helper.compat.ActivityThreadCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PluginCallbackHook extends Hook {

    private static final String TAG = PluginCallbackHook.class.getSimpleName();
    private List<PluginCallback> mCallbacks = new ArrayList<PluginCallback>(1);

    public PluginCallbackHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected BaseClassHandle createHookHandle() {
        return null;
    }

    @Override
    public void setEnable(boolean enable, boolean reinstallHook) {
        if (reinstallHook) {
            try {
                onInstall(null);
            } catch (Throwable throwable) {
                Log.i(TAG, "setEnable onInstall fail", throwable);
            }
        }

        for (PluginCallback callback : mCallbacks) {
            callback.setEnable(enable);
        }
        super.setEnable(enable,reinstallHook);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Object target = ActivityThreadCompat.currentActivityThread();
        Class ActivityThreadClass = ActivityThreadCompat.activityThreadClass();

        /*替换ActivityThread.mH.mCallback，拦截组件调度消息*/
        Field mHField = FieldUtils.getField(ActivityThreadClass, "mH");
        Handler handler = (Handler) FieldUtils.readField(mHField, target);
        Field mCallbackField = FieldUtils.getField(Handler.class, "mCallback");
        //*这里读取出旧的callback并处理*/
        Object mCallback = FieldUtils.readField(mCallbackField, handler);
        if (!PluginCallback.class.isInstance(mCallback)) {
            PluginCallback value = mCallback != null ? new PluginCallback(mHostContext, handler, (Handler.Callback) mCallback) : new PluginCallback(mHostContext, handler, null);
            value.setEnable(isEnable());
            mCallbacks.add(value);
            FieldUtils.writeField(mCallbackField, handler, value);
            Log.i(TAG, "PluginCallbackHook has installed");
        } else {
            Log.i(TAG, "PluginCallbackHook has installed,skip");
        }
    }
}
