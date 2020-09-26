package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamShowKeyboard;

import org.greenrobot.eventbus.EventBus;


public class HybridActionShowKeyboard extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamShowKeyboard hybridParam = mGson.fromJson(params, HybridParamShowKeyboard.class);
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        hybridParam.tagname ="showKeyboard";
        EventBus.getDefault().post(hybridParam);
    }
}
