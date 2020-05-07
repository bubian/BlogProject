package com.pds.dumpdex.dump;

import com.pds.dumpdex.BuildConfig;
import com.pds.dumpdex.Native;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * OreoDump
 *
 * @author WrBug
 * @since 2018/3/23
 */
public class OreoDump {

    public static void log(String txt) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        XposedBridge.log("dumpdex-> " + txt);
    }

    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) {
        Native.dump(lpparam.packageName);
    }
}
