package com.pds.env.med;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/1 5:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PatientEnv {
    // 测试环境
    private static final String APP_QA = "https://patient-medication-qa.medlinker.com";

    public static String getAppEnv() {
        return APP_QA;
    }
}
