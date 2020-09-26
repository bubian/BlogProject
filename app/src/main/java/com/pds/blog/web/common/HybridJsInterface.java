package com.pds.blog.web.common;

import android.webkit.JavascriptInterface;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 16:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HybridJsInterface {
    public static final String JSInterface = "HybridJSInterface";

    @JavascriptInterface
    public void stringByEvaluatingJavaScriptFromString(String tagname, String value) {
        if (!"true".equals(value) && "back".equals(tagname)) {

        }
    }
}
