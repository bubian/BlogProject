package com.pds.web.core;

import android.webkit.JavascriptInterface;

import com.pds.web.param.HybridParamBack;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by vane on 16/7/9.
 */

public class HybridJsInterface {

    @JavascriptInterface
    final public void stringByEvaluatingJavaScriptFromString(String tagname, String value) {
        if (!"true".equals(value) && "back".equals(tagname)) {
            EventBus.getDefault().post(new HybridParamBack());
        }
    }
}
