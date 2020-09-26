package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamBack;

import org.greenrobot.eventbus.EventBus;


public class HybridActionBack extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamBack hybridParam = new HybridParamBack();
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
