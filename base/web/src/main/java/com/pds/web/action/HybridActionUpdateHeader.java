package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamUpdateHeader;

import org.greenrobot.eventbus.EventBus;


public class HybridActionUpdateHeader extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamUpdateHeader hybridParam = mGson.fromJson(params, HybridParamUpdateHeader.class);
        hybridParam.id = webviewHashCode;
        hybridParam.setHybridParamCallbackId(webviewHashCode);

        EventBus.getDefault().post(hybridParam);
    }
}
