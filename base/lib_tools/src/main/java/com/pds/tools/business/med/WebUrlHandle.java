package com.pds.tools.business.med;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/1 11:23 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class WebUrlHandle {
    private static final String TAG = "WebUrlHandle";
    private static final String URL_FIELD = "url";
    public static void doUrl() {
        doUrl(null);
    }

    public static void doUrl(Object o) {
        Log.d(TAG, "doUrl start");
        try {
            if (null == o) {
                return;
            }

            Class<?> clazz = o.getClass();
            Field field = clazz.getDeclaredField(URL_FIELD);
            if (null == field) {
                return;
            }
            field.setAccessible(true);
            field.set(o,"change url");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
