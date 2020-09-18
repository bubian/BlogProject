package com.pds.util.app;

import android.content.Context;
import com.pds.util.BuildConfig;

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
}
