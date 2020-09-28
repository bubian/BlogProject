package com.pds.kotlin.study.net;


/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/23 10:19 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiEnv {

    // 测试环境
    private static final String APP_QA = "https://patient-medication-qa.medlinker.com";

    public static String getAppEnv() {
        return APP_QA;
    }
}
