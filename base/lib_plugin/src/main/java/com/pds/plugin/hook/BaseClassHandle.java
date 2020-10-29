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

import com.pds.plugin.helper.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseClassHandle {

    protected Context mHostContext;

    protected Map<String, BaseMethodHandle> sHookedMethodHandlers = new HashMap<>(5);

    public BaseClassHandle(Context hostContext) {
        mHostContext = hostContext;
        init();
    }

    protected abstract void init();

    public Set<String> getHookedMethodNames(){
        return sHookedMethodHandlers.keySet();
    }

    public BaseMethodHandle getHookedMethodHandler(Method method) {
        if (method != null) {
            return sHookedMethodHandlers.get(method.getName());
        } else {
            return null;
        }
    }

    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return null;
    }

    protected BaseMethodHandle newBaseHandler() throws ClassNotFoundException {
        return null;
    }

    protected void addAllMethodFromHookedClass(){
        try {
            Class clazz = getHookedClass();
            if(clazz!=null){
                Method[] methods = clazz.getDeclaredMethods();
                if(methods!=null && methods.length>0){
                    for (Method method : methods) {
                        if(Modifier.isPublic(method.getModifiers()) && !sHookedMethodHandlers.containsKey(method.getName())){
                            sHookedMethodHandlers.put(method.getName(),newBaseHandler());
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.w(getClass().getSimpleName(),"init addAllMethodFromHookedClass error",e);
        }
    };
}
