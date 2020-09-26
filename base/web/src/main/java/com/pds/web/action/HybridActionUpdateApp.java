package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamUpdateApp;

import org.greenrobot.eventbus.EventBus;

public class HybridActionUpdateApp extends HybridAction {
    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamUpdateApp hybridParam = mGson.fromJson(params, HybridParamUpdateApp.class);
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
