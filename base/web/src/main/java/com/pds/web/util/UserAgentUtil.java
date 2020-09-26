package com.pds.web.util;

import com.blog.pds.net.BuildConfig;

public class UserAgentUtil {

    public static String getMedUserAgent(String defaultUserAgent) {
        //todo
        String useragent = defaultUserAgent
                .concat("").concat(BuildConfig.VERSION_NAME)
                .concat("").concat(BuildConfig.VERSION_NAME.concat(")"));
        return useragent;
    }
}
