package com.pds.util.path;

import android.content.Context;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/1 8:49 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DiskPath {

    public static String getCacheDir(Context context){
        return context.getCacheDir().getAbsolutePath();
    }
}
