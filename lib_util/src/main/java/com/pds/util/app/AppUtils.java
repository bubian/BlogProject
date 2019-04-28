package com.pds.util.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * @author: pengdaosong
 * CreateTime:  2019/4/11 8:13 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 * 参考：http://www.trinea.cn/android/android-whether-debug-mode-why-buildconfig-debug-always-false
 */
public class AppUtils {
    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug.booleanValue();
    }

    /**
     * 在自己的 Application 内调用进行初始化
     *
     * @param context
     */
    public static void syncIsDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

}
