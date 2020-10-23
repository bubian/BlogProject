package com.pds.web;


import com.blog.pds.net.BuildConfig;

public class CommonPath {
    static final String CHECK_VERSION_QA_URL = "";
    static final String CHECK_VERSION_URL = "";

    public static String getH5Host() {
//        switch (BuildConfig.API_URL_TYPE) {
//            case 3://线上环境
//                return "";
//            case 0://DEV
//                return "";
//            case 4://QA
//                return "";
//            default:
//                break;
//        }
        return "";
    }

    public static String getH5CookieDomain() {
//        switch (BuildConfig.API_URL_TYPE) {
//            case 3://线上环境
//                return "";
//            case 0://DEV
//                return "";
//            case 4://QA
//                return "";
//            default:
//                break;
//        }
        return "";
    }

    public static String getCheckversionUrl() {
        String url = CHECK_VERSION_QA_URL;
//        switch (BuildConfig.API_URL_TYPE) {
//            case 3:// 线上
//                url = CHECK_VERSION_URL;
//                break;
//            case 0:
//                break;
//            case 4:
//                break;
//
//            default:
//                break;
//        }
        return url;
    }
}
