package com.pds.storage.mmkv;

import com.tencent.mmkv.MMKV;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 11:53 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class MMKVManager {
    public static MMKV mmkv(){
        return MMKV.defaultMMKV();
    }
}
