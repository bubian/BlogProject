package com.pds.tools.business.med.env;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/23 10:19 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PatientEnv {

    // 线上环境
    private static final String APP_ONLINE = "https://patient-medication.medlinker.com";
    // 测试环境
    private static final String APP_QA = "https://patient-medication-qa.medlinker.com";
    // 开发环境
    private static final String APP_DEV = "http://patient-medication-dev.medlinker.com";

    public static String getAppEnv(int env) {
        switch (env) {
            case 3:
                return APP_ONLINE;
            case 4:
                return APP_QA;
            default:
                return APP_DEV;
        }
    }
}
