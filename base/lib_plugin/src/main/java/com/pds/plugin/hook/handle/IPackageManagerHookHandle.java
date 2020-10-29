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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import com.pds.plugin.hook.BaseMethodHandle;
import com.pds.plugin.hook.BaseClassHandle;
import com.pds.plugin.pm.PluginManager;
import com.pds.plugin.reflect.MethodUtils;
import com.pds.plugin.helper.Log;
import com.pds.plugin.helper.compat.ParceledListSliceCompat;

import java.lang.reflect.Method;
import java.util.List;

public class IPackageManagerHookHandle extends BaseClassHandle {

    private static final String TAG = IPackageManagerHookHandle.class.getSimpleName();

    public IPackageManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("getPackageInfo", new getPackageInfo(mHostContext));
        sHookedMethodHandlers.put("getApplicationInfo", new getApplicationInfo(mHostContext));
        sHookedMethodHandlers.put("resolveIntent", new resolveIntent(mHostContext));
        sHookedMethodHandlers.put("queryIntentActivities", new queryIntentActivities(mHostContext));
        sHookedMethodHandlers.put("getInstallerPackageName", new getInstallerPackageName(mHostContext));
    }
    private class getInstallerPackageName extends BaseMethodHandle {
        public getInstallerPackageName(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        setFakedResult(mHostContext.getPackageName());
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
    private class getPackageInfo extends BaseMethodHandle {
        public getPackageInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null) {
                final int index0 = 0, index1 = 1;
                String packageName = null;
                if (args.length > index0) {
                    if (args[index0] != null && args[index0] instanceof String) {
                        packageName = (String) args[index0];
                    }
                }
                int flags = 0;
                if (args.length > index1) {
                    if (args[index1] != null && args[index1] instanceof Integer) {
                        flags = (Integer) args[index1];
                    }
                }
                if (packageName != null) {
                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = PluginManager.getInstance().getPackageInfo(packageName, flags);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (packageInfo != null) {
                        setFakedResult(packageInfo);
                        return true;
                    } else {
                        Log.i(TAG, "getPackageInfo(%s) fail,pkginfo is null", packageName);
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }





    private class getApplicationInfo extends BaseMethodHandle {
        public getApplicationInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof String && args[index1] instanceof Integer) {
                    String packageName = (String) args[index0];
                    int flags = (Integer) args[index1];
                    ApplicationInfo info = PluginManager.getInstance().getApplicationInfo(packageName, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }




    private class resolveIntent extends BaseMethodHandle {
        public resolveIntent(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                if (intent != null) {
                    ResolveInfo info = PluginManager.getInstance().resolveIntent(intent, null, 0);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class queryIntentActivities extends BaseMethodHandle {
        public queryIntentActivities(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            if (args != null) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }
                if (intent != null) {
                    List<ResolveInfo> infos = PluginManager.getInstance().queryIntentActivities(intent,null, 0);
                    if (infos != null && infos.size() > 0) {
                        Method getListMethod = ParceledListSliceCompat.Class().getMethod("getList");
                        List data = (List) getListMethod.invoke(invokeResult);
                        data.addAll(infos);
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getInstalledPackages extends BaseMethodHandle {
        public getInstalledPackages(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            try {
                if (invokeResult != null && ParceledListSliceCompat.isParceledListSlice(invokeResult)) {
                    android.util.Log.i(TAG, "getInstalledPackages: 1");
                        Method getListMethod = MethodUtils.getAccessibleMethod(invokeResult.getClass(), "getList");
                        List data = (List) getListMethod.invoke(invokeResult);
                        final int index0 = 0;
                        if (args.length > index0 && args[index0] instanceof Integer) {
                            int flags = (Integer) args[index0];
                            List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(flags);
                            if (infos != null && infos.size() > 0) {
                                data.addAll(infos);
                            }
                        }
                } else if (invokeResult instanceof List) {
                    android.util.Log.i(TAG, "getInstalledPackages: 2");
                    final int index0 = 0;
                    if (args.length > index0 && args[index0] instanceof Integer) {
                        int flags = (Integer) args[index0];
                        List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(flags);
                        if (infos != null && infos.size() > 0) {
                            List old = (List) invokeResult;
                            old.addAll(infos);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }
}
