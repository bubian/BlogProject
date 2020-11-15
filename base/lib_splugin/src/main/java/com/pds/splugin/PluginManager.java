package com.pds.splugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginManager {
    private PackageInfo packageInfo;
    private Resources resources;
    private Context context;
    private DexClassLoader dexClassLoader;
    private static final PluginManager ourInstance = new PluginManager();

    private volatile int state;

    public static PluginManager getInstance() {
        return ourInstance;
    }

    private PluginManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void loadPath(Context context,String name) {
        state = 1;
        File filesDir = context.getDir("plugin", Context.MODE_PRIVATE);
        String path = new File(filesDir, name).getAbsolutePath();
        PackageManager packageManager = context.getPackageManager();
        packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsolutePath(), null, context.getClassLoader());
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
            state = 2;
        } catch (Exception e) {
            state = 3;
            e.printStackTrace();
        }
    }

    public Resources getResources() {
        return resources;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public boolean isLoadComplete(){
        return 2 == state;
    }
}
