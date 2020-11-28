package com.pds.debug;

import android.content.Context;
import android.content.Intent;

import com.pds.debug.utils.XdLog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

public class XdDebugAdapter {
    private static final String TAG = XdDebugAdapter.class.getSimpleName();

    public static void postToMainLooper(Runnable runnable) {
        ModuleDebug.instance().postToMainLooper(runnable);
    }

    public static Context getAppContext() {
        return ModuleDebug.instance().appContext();
    }

    public static String getPackageName() {
        return ModuleDebug.instance().appContext().getPackageName();
    }

    public static void startActivity(Intent intent) {
        ModuleDebug.instance().appContext().startActivity(intent);
    }

    private static Map<Integer, String> _intentFlags;

    private static String parseIntentFlags(int flags) {
        if (_intentFlags == null) {
            _intentFlags = new TreeMap<Integer, String>();
            Class<Intent> intentClass = Intent.class;
            Field[] fields = intentClass.getFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                String flagName = field.getName();
                if (Modifier.isStatic(modifiers) &
                        field.getType().getName().equals("int") &
                        flagName.startsWith("FLAG_")
                ) {
                    try {
                        _intentFlags.put(field.getInt(null), flagName.substring(5));
                    } catch (IllegalAccessException e) {
                        XdLog.e(TAG, e);
                    }
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, String> entry : _intentFlags.entrySet()) {
            int key = entry.getKey();
            if ((key & flags) == key) {
                flags &= ~key;
                if (result.length() > 0) {
                    result.append("|");
                }
                result.append(entry.getValue());
            }
        }
        if (flags != 0) {
            if (result.length() > 0) {
                result.append("|");
            }
            result.append(Integer.toHexString(flags));
        }
        return result.toString();
    }
}
