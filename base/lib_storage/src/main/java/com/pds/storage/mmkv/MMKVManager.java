package com.pds.storage.mmkv;

import android.app.Application;
import android.content.SharedPreferences;

import com.pds.storage.ModuleStorage;
import com.pds.storage.mmkv.id.MMKVId;
import com.tencent.mmkv.MMKV;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 11:53 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class MMKVManager {

    public static void init(Application application) {
        MMKV.initialize(application);
    }

    public static MMKV mmkv() {
        return MMKV.defaultMMKV();
    }

    // 如果不同业务需要区别存储，也可以单独创建自己的实例
    public static MMKV mmkvUser() {
        return MMKV.mmkvWithID(MMKVId.USER_ID);
    }

    // 如果业务需要多进程访问，那么在初始化的时候加上标志位 MMKV.MULTI_PROCESS_MODE
    public static MMKV mmkvCP() {
        return MMKV.mmkvWithID(MMKVId.USER_ID, MMKV.MULTI_PROCESS_MODE);
    }

    /**
     * MMKV 提供了 importFromSharedPreferences() 函数，可以比较方便地迁移数据过来。
     * <p>
     * MMKV 还额外实现了一遍 SharedPreferences、SharedPreferences.Editor 这两个 interface，在迁移的时候只需两三行代码即可，其他 CRUD 操作代码都不用改。
     *
     * @return
     */
    public static SharedPreferences.Editor migrate(String id, int mode) {
        MMKV preferences = MMKV.mmkvWithID(id);
        // 迁移旧数据
        SharedPreferences old_man = ModuleStorage.instance().appContext().getSharedPreferences(id, mode);
        preferences.importFromSharedPreferences(old_man);
        old_man.edit().clear().commit();
        return preferences.edit();
    }
}
