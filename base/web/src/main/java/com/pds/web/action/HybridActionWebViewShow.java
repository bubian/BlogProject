package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamWebViewShow;

import org.greenrobot.eventbus.EventBus;

public class HybridActionWebViewShow extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamWebViewShow hybridParam = new HybridParamWebViewShow();
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
