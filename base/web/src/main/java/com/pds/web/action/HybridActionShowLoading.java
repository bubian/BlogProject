package com.pds.web.action;


import android.content.Context;

import com.pds.web.param.HybridParamShowLoading;

import org.greenrobot.eventbus.EventBus;


public class HybridActionShowLoading extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamShowLoading hybridParam = mGson.fromJson(params, HybridParamShowLoading.class);
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
