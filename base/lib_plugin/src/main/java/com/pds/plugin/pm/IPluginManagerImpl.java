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
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Binder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.pds.plugin.am.BaseActivityManagerService;
import com.pds.plugin.am.MyActivityManagerService;
import com.pds.plugin.core.PluginDirHelper;
import com.pds.plugin.pm.parser.IntentMatcher;
import com.pds.plugin.pm.parser.PluginPackageParser;
import com.pds.plugin.helper.Log;
import com.pds.plugin.helper.Utils;
import com.pds.plugin.helper.compat.NativeLibraryHelperCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 此服务模仿系统的PackageManagerService，提供对插件简单的管理服务。
 */
public class IPluginManagerImpl extends IPluginManager.Stub {

    private static final String TAG = IPluginManagerImpl.class.getSimpleName();

    private Map<String, PluginPackageParser> mPluginCache = Collections.synchronizedMap(new HashMap<String, PluginPackageParser>(20));

    private Context mContext;

    private AtomicBoolean mHasLoadedOk = new AtomicBoolean(false);
    private final Object mLock = new Object();

    private BaseActivityManagerService mActivityManagerService;

    private Set<String> mHostRequestedPermission = new HashSet<String>(10);

    private Map<String, Signature[]> mSignatureCache = new HashMap<String, Signature[]>();

    public IPluginManagerImpl(Context context) {
        mContext = context;
        mActivityManagerService = new MyActivityManagerService(mContext);
    }

    public void onCreate() {
        new Thread() {
            @Override
            public void run() {
                onCreateInner();
            }
        }.start();
    }

    private void onCreateInner() {
        loadAllPlugin(mContext);
        loadHostRequestedPermission();
        try {
            mHasLoadedOk.set(true);
            synchronized (mLock) {
                mLock.notifyAll();
            }
        } catch (Exception e) {
        }
    }

    private void loadHostRequestedPermission() {
        android.util.Log.i(TAG, "loadHostRequestedPermission: ");
        try {
            mHostRequestedPermission.clear();
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pms = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (pms != null && pms.requestedPermissions != null && pms.requestedPermissions.length > 0) {
                for (String requestedPermission : pms.requestedPermissions) {
                    mHostRequestedPermission.add(requestedPermission);
                }
            }
        } catch (Exception e) {
        }
    }


    private void loadAllPlugin(Context context) {
        long b = System.currentTimeMillis();
        ArrayList<File> apkfiles = null;
        try {
            apkfiles = new ArrayList<File>();
            File baseDir = new File(PluginDirHelper.getBaseDir(context));
            File[] dirs = baseDir.listFiles();
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    File file = new File(dir, "apk/base-1.apk");
                    if (file.exists()) {
                        apkfiles.add(file);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "scan a apk file error", e);
        }

        Log.i(TAG, "Search apk cost %s ms", (System.currentTimeMillis() - b));
        b = System.currentTimeMillis();

        if (apkfiles != null && apkfiles.size() > 0) {
            for (File pluginFile : apkfiles) {
                long b1 = System.currentTimeMillis();
                try {
                    PluginPackageParser pluginPackageParser = new PluginPackageParser(mContext, pluginFile);
                    Signature[] signatures = readSignatures(pluginPackageParser.getPackageName());
                    if (signatures == null || signatures.length <= 0) {
                        pluginPackageParser.collectCertificates(0);
                        PackageInfo info = pluginPackageParser.getPackageInfo(PackageManager.GET_SIGNATURES);
                    } else {
                        mSignatureCache.put(pluginPackageParser.getPackageName(), signatures);
                        pluginPackageParser.writeSignature(signatures);
                    }
                    if (!mPluginCache.containsKey(pluginPackageParser.getPackageName())) {
                        mPluginCache.put(pluginPackageParser.getPackageName(), pluginPackageParser);
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "parse a apk file error %s", e, pluginFile.getPath());
                } finally {
                    Log.i(TAG, "Parse %s apk cost %s ms", pluginFile.getPath(), (System.currentTimeMillis() - b1));
                }
            }
        }

        Log.i(TAG, "Parse all apk cost %s ms", (System.currentTimeMillis() - b));
        b = System.currentTimeMillis();

        try {
            mActivityManagerService.onCreate(IPluginManagerImpl.this);
        } catch (Throwable e) {
            Log.e(TAG, "mActivityManagerService.onCreate", e);
        }

        Log.i(TAG, "ActivityManagerService.onCreate %s ms", (System.currentTimeMillis() - b));
    }

    private void enforcePluginFileExists() throws RemoteException {
        android.util.Log.i(TAG, "enforcePluginFileExists: ");
        List<String> removedPkg = new ArrayList<>();
        for (String pkg : mPluginCache.keySet()) {
            PluginPackageParser parser = mPluginCache.get(pkg);
            File pluginFile = parser.getPluginFile();
            if (pluginFile != null && pluginFile.exists()) {
                //DO NOTHING
            } else {
                removedPkg.add(pkg);
            }
        }
        for (String pkg : removedPkg) {
            deletePackage(pkg, 0);
        }
    }


    @Override
    public boolean waitForReady() {
        android.util.Log.i(TAG, "waitForReady: ");
        waitForReadyInner();
        return true;
    }

    private void waitForReadyInner() {
        android.util.Log.i(TAG, "waitForReadyInner: ");
        if (!mHasLoadedOk.get()) {
            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }



    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        waitForReadyInner();
        try {
            String pkg = getAndCheckCallingPkg(packageName);
            if (pkg != null && !TextUtils.equals(packageName, mContext.getPackageName())) {
                enforcePluginFileExists();
                PluginPackageParser parser = mPluginCache.get(pkg);
                if (parser != null) {
                    PackageInfo packageInfo = parser.getPackageInfo(flags);
                    if (packageInfo != null && (flags & PackageManager.GET_SIGNATURES) != 0 && packageInfo.signatures == null) {
                        packageInfo.signatures = mSignatureCache.get(packageName);
                    }
                    return packageInfo;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


    @Override
    public boolean isPluginPackage(String packageName) throws RemoteException {
        android.util.Log.i(TAG, "isPluginPackage: ");
        waitForReadyInner();
        enforcePluginFileExists();
        return mPluginCache.containsKey(packageName);
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws RemoteException {
        android.util.Log.i(TAG, "getActivityInfo: ");
        waitForReadyInner();
        try {
            String pkg = getAndCheckCallingPkg(className.getPackageName());
            if (pkg != null) {
                enforcePluginFileExists();
                PluginPackageParser parser = mPluginCache.get(className.getPackageName());
                if (parser != null) {
                    return parser.getActivityInfo(className, flags);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }








    private String getAndCheckCallingPkg(String pkg) {
        return pkg;
//            return pkg;
//        } else {
//            if (!pkgInPid(Binder.getCallingPid(), pkg)) {
//                return null;
//            } else {
//                return pkg;
//            }
//        }
    }



    @Override
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException {
        android.util.Log.i(TAG, "resolveIntent: ");
        waitForReadyInner();
        try {
            enforcePluginFileExists();

                List<ResolveInfo> infos = IntentMatcher.resolveIntent(mContext, mPluginCache, intent, resolvedType, flags);
                if (infos != null && infos.size() > 0) {
                    return IntentMatcher.findBest(infos);
                }

        } catch (Exception e) {
        }
        return null;
    }


    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException {
        android.util.Log.i(TAG, "queryIntentActivities: ");
        waitForReadyInner();
        try {
            enforcePluginFileExists();
            return IntentMatcher.resolveActivityIntent(mContext, mPluginCache, intent, resolvedType, flags);
        } catch (Exception e) {
        }
        return null;
    }





    @Override
    public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException {
        android.util.Log.i(TAG, "getInstalledPackages: ");
        waitForReadyInner();
        try {
            enforcePluginFileExists();
            List<PackageInfo> infos = new ArrayList<PackageInfo>(mPluginCache.size());
                for (PluginPackageParser pluginPackageParser : mPluginCache.values()) {
                    infos.add(pluginPackageParser.getPackageInfo(flags));
                }
            return infos;
        } catch (Exception e) {
        }
        return null;
    }







    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException {
        android.util.Log.i(TAG, "getApplicationInfo: ");
        waitForReadyInner();
        try {
            if (TextUtils.equals(packageName, mContext.getPackageName())) {
                return null;
            }
            PluginPackageParser parser = mPluginCache.get(packageName);
            if (parser != null) {
                return parser.getApplicationInfo(flags);
            }
        } catch (Exception e) {
        }

        return null;
    }


    @Override
    public int installPackage(String filepath, int flags) throws RemoteException {
        android.util.Log.i(TAG, "installPackage: ");
        //install plugin
        String apkfile = null;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filepath, 0);
            apkfile = PluginDirHelper.getPluginApkFile(mContext, info.packageName);
            new File(apkfile).delete();
            Utils.copyFile(filepath, apkfile);
            PluginPackageParser parser = new PluginPackageParser(mContext, new File(apkfile));
            parser.collectCertificates(0);
            PackageInfo pkgInfo = parser.getPackageInfo(PackageManager.GET_PERMISSIONS | PackageManager.GET_SIGNATURES);
            if (copyNativeLibs(mContext, apkfile, parser.getApplicationInfo(0)) < 0) {
                new File(apkfile).delete();
            }
            mPluginCache.put(parser.getPackageName(), parser);
            mActivityManagerService.onPkgInstalled(mPluginCache, parser, parser.getPackageName());
        } catch (Exception e) {
        }
        return 0;
    }


    private Signature[] readSignatures(String packageName) {
        android.util.Log.i(TAG, "readSignatures: ");
        List<String> fils = PluginDirHelper.getPluginSignatureFiles(mContext, packageName);
        List<Signature> signatures = new ArrayList<Signature>(fils.size());
        int i = 0;
        for (String file : fils) {
            try {
                byte[] data = Utils.readFromFile(new File(file));
                if (data != null) {
                    Signature sin = new Signature(data);
                    signatures.add(sin);
                    Log.i(TAG, "Read %s signature of %s,md5=%s", packageName, i, Utils.md5(sin.toByteArray()));
                } else {
                    Log.i(TAG, "Read %s signature of %s FAIL", packageName, i);
                    return null;
                }
                i++;
            } catch (Exception e) {
                Log.i(TAG, "Read %s signature of %s FAIL", e, packageName, i);
                return null;
            }
        }
        return signatures.toArray(new Signature[signatures.size()]);
    }





    private int copyNativeLibs(Context context, String apkfile, ApplicationInfo applicationInfo) throws Exception {
        String nativeLibraryDir = PluginDirHelper.getPluginNativeLibraryDir(context, applicationInfo.packageName);
        return NativeLibraryHelperCompat.copyNativeBinaries(new File(apkfile), new File(nativeLibraryDir));
    }


    @Override
    public int deletePackage(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginCache.containsKey(packageName)) {
                PluginPackageParser parser;
                synchronized (mPluginCache) {
                    parser = mPluginCache.remove(packageName);
                }
                Utils.deleteDir(PluginDirHelper.makePluginBaseDir(mContext, packageName));
                mActivityManagerService.onPkgDeleted(mPluginCache, parser, packageName);
                mSignatureCache.remove(packageName);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    @Override
    public List<ActivityInfo> getReceivers(String packageName, int flags) throws RemoteException {
        android.util.Log.i(TAG, "getReceivers: ");
        try {
            String pkg = getAndCheckCallingPkg(packageName);
            if (pkg != null) {
                PluginPackageParser parser = mPluginCache.get(packageName);
                if (parser != null) {
                    return new ArrayList<ActivityInfo>(parser.getReceivers());
                }
            }
        } catch (Exception e) {
            RemoteException remoteException = new RemoteException();
            remoteException.setStackTrace(e.getStackTrace());
            throw remoteException;
        }
        return new ArrayList<ActivityInfo>(0);
    }

    @Override
    public List<IntentFilter> getReceiverIntentFilter(ActivityInfo info) throws RemoteException {
        android.util.Log.i(TAG, "getReceiverIntentFilter: ");
        try {
            String pkg = getAndCheckCallingPkg(info.packageName);
            if (pkg != null) {
                PluginPackageParser parser = mPluginCache.get(info.packageName);
                if (parser != null) {
                    List<IntentFilter> filters = parser.getReceiverIntentFilter(info);
                    if (filters != null && filters.size() > 0) {
                        return new ArrayList<IntentFilter>(filters);
                    }
                }
            }
            return new ArrayList<IntentFilter>(0);
        } catch (Exception e) {
            RemoteException remoteException = new RemoteException();
            remoteException.setStackTrace(e.getStackTrace());
            throw remoteException;
        }
    }

    //////////////////////////////////////
    //
    //  THIS API FOR ACTIVITY MANAGER
    //
    //////////////////////////////////////

    @Override
    public ActivityInfo selectStubActivityInfo(ActivityInfo pluginInfo) throws RemoteException {
        android.util.Log.i(TAG, "selectStubActivityInfo: ");
        return mActivityManagerService.selectStubActivityInfo(Binder.getCallingPid(), Binder.getCallingUid(), pluginInfo);
    }

    @Override
    public ActivityInfo selectStubActivityInfoByIntent(Intent intent) throws RemoteException {
        android.util.Log.i(TAG, "selectStubActivityInfoByIntent: ");
        ActivityInfo ai = null;
        if (intent.getComponent() != null) {
            ai = getActivityInfo(intent.getComponent(), 0);
        } else {
            ResolveInfo resolveInfo = resolveIntent(intent, intent.resolveTypeIfNeeded(mContext.getContentResolver()), 0);
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                ai = resolveInfo.activityInfo;
            }
        }

        if (ai != null) {
            return selectStubActivityInfo(ai);
        }
        return null;
    }






    @Override
    public String getProcessNameByPid(int pid) throws RemoteException {
        android.util.Log.i(TAG, "getProcessNameByPid: ");
        return mActivityManagerService.getProcessNameByPid(pid);
    }






    @Override
    public void onActivityCreated(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        android.util.Log.i(TAG, "onActivityCreated: ");
        mActivityManagerService.onActivityCreated(Binder.getCallingPid(), Binder.getCallingUid(), stubInfo, targetInfo);
    }

    @Override
    public void onActivityDestory(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        android.util.Log.i(TAG, "onActivityDestory: ");
        mActivityManagerService.onActivityDestroy(Binder.getCallingPid(), Binder.getCallingUid(), stubInfo, targetInfo);
    }



    @Override
    public void reportMyProcessName(String stubProcessName, String targetProcessName, String targetPkg) throws RemoteException {
        android.util.Log.i(TAG, "reportMyProcessName: ");
        mActivityManagerService.onReportMyProcessName(Binder.getCallingPid(), Binder.getCallingUid(), stubProcessName, targetProcessName, targetPkg);
    }

    public void onDestroy() {
        android.util.Log.i(TAG, "onDestroy: ");
        mActivityManagerService.onDestroy();
    }



}
