package com.pds.tools;

import android.app.Application;

import me.weishu.reflection.Reflection;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/1 9:16 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 反射加强工具类
 *
 * 参考：https://github.com/tiann/FreeReflection
 */
public class ReflectionManager {
    public static void init(Application application) {
        Reflection.unseal(application);
    }
}
