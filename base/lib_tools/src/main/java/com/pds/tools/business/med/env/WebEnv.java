package com.pds.tools.business.med.env;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/28 10:38 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class WebEnv {

    private static final int TYPE_DEV = 0;
    private static final int TYPE_ONLINE = 3;
    private static final int TYPE_QA = 4;
    // 线上环境
    private static final String APP_ONLINE = "https://web.medlinker.com";
    // 测试环境
    private static final String APP_QA = "https://web-qa.medlinker.com";
    // 开发环境
    private static final String APP_DEV = "http://web-dev.medlinker.com";

    public static String getWebEnv(int env) {
        switch (env) {
            case TYPE_ONLINE:
                return APP_ONLINE;
            case TYPE_QA:
                return APP_QA;
            default:
                return APP_DEV;
        }
    }
}
