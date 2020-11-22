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

package com.pds.plugin;

import android.app.Application;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModulePlugin {

    private Application mApplication;

    /**
     * 不一定要在启动的时候初始化，根据业务而定
     *
     * @param application
     */
    public static void init(Application application) {
        instance().mApplication = application;
        PluginHelper.getInstance().applicationOnCreate(application);
    }

    public static final ModulePlugin instance() {
        return Lazy.INSTANCE;
    }

    private static final class Lazy {
        private static final ModulePlugin INSTANCE = new ModulePlugin();
    }

    public Application appContext() {
        if (null == mApplication) {
            mApplication = findApplicationFromApp();
        }

        if (null == mApplication) {
            mApplication = findApplicationFromSystem();
        }

        if (null == mApplication) {
            throw new NullPointerException("ModuleStorage appContext is null");
        }
        return mApplication;
    }

    private Application findApplicationFromApp() {
        try {
            Class<?> clazz = Class.forName(BuildConfig.APPLICATION_CLASS_NAME);
            Field field = clazz.getField(BuildConfig.APPLICATION_VAR_NAME);
            field.setAccessible(true);
            Object app = field.get(clazz);
            if (app instanceof Application) {
                return (Application) app;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Application findApplicationFromSystem() {
        try {
            Method method = Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
            method.setAccessible(true);
            Object at = method.invoke(null);
            Object app = at.getClass().getMethod("getApplication").invoke(at);
            if (app instanceof Application) {
                return (Application) app;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
