package com.pds.storage.sp;

import com.pds.storage.ModuleStorage;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 12:54 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PreferencesManager {

    public static final String DEFAULT_NAME = "Lib_tools";

    public static boolean getBoolean(String key) {
        return PreferencesHelper.getBoolean(ModuleStorage.instance().appContext(), DEFAULT_NAME, key, false);
    }
}
