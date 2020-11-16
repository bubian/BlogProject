package com.pds.util.app;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.pds.util.BuildConfig;

import java.util.List;

/**
 * @author pengdaosong
 */
public final class PackageUtils {

    public static int getSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionCode() {
        int versionCode = BuildConfig.VERSION_CODE;
        return versionCode;
    }

    public static String getVersionName() {
        String versionName = BuildConfig.VERSION_NAME;
        return versionName;
    }

    public static String getPackageName(Context mContext) {
        return "BuildConfig.APPLICATION_ID";
    }

    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (null != processInfos) {
            String mainProcessName = context.getPackageName();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
