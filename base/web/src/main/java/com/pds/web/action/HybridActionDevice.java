package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamDevice;

import org.greenrobot.eventbus.EventBus;


public class HybridActionDevice extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamDevice hybridParam = new HybridParamDevice();
        hybridParam.id = webviewHashCode;
        hybridParam.tagname = "getdevicenum";
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
