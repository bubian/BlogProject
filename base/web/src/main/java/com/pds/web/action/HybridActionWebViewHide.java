package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamWebViewHide;

import org.greenrobot.eventbus.EventBus;


public class HybridActionWebViewHide extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamWebViewHide hybridParam = new HybridParamWebViewHide();
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
