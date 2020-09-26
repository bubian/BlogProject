package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamAjax;

import org.greenrobot.eventbus.EventBus;

public class HybridActionAjaxGet extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamAjax hybridParam = mGson.fromJson(params, HybridParamAjax.class);
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
