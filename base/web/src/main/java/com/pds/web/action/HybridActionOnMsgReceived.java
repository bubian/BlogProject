package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamOnMsgReceived;

import org.greenrobot.eventbus.EventBus;

public class HybridActionOnMsgReceived extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamOnMsgReceived hybridParam = new HybridParamOnMsgReceived();
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        hybridParam.tagname = "onmsgreceived";
        EventBus.getDefault().post(hybridParam);
    }
}
