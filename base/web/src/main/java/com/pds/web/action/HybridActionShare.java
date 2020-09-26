package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamShare;

import org.greenrobot.eventbus.EventBus;


public class HybridActionShare extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamShare hybridParam = mGson.fromJson(params, HybridParamShare.class);
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
