package com.pds.env.blog;

import com.pds.env.EnvVar;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/1 5:27 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AppEnv {
    public static String getAppEnv() {
        switch (com.pds.env.BuildConfig.API_URL_TYPE) {
            case EnvVar.ONLINE:
                return "http://47.104.91.148";
            default:
                return "http://47.104.91.148";
        }
    }
}
