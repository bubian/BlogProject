package com.pds.web.core;

/**
 * Created by vane on 16/8/24.
 */

public class HybridPath {
    static final String CHECK_VERSION_QA_URL = "baidu.com";
    public static String getH5Host() {
        return "baidu.com";
    }

    public static String getH5CookieDomain() {
        return "baidu.com";
    }

    public static String getCheckversionUrl() {
        String url = CHECK_VERSION_QA_URL;
        return url;
    }
}