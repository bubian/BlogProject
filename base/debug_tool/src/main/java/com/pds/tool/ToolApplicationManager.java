package com.pds.tool;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-07 15:40
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ToolApplicationManager {

    public static void onCreate(Application application) {
        Stetho.initializeWithDefaults(application);
    }
}
