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

package com.pds.plugin.hook;

import android.content.Context;

public abstract class Hook {

    private boolean mEnable = false;

    protected Context mHostContext;
    protected BaseClassHandle mHookHandles;

    public void setEnable(boolean enable, boolean reInstallHook) {
        this.mEnable = enable;
    }

    public final void setEnable(boolean enable) {
        setEnable(enable, false);
    }

    public boolean isEnable() {
        return mEnable;
    }


    protected Hook(Context hostContext) {
        mHostContext = hostContext;
        mHookHandles = createHookHandle();
    }

    protected abstract BaseClassHandle createHookHandle();


    protected abstract void onInstall(ClassLoader classLoader) throws Throwable;

    protected void onUnInstall(ClassLoader classLoader) throws Throwable {

    }
}
