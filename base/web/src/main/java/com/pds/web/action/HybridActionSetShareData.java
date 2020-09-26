package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamSetShareData;

import org.greenrobot.eventbus.EventBus;


public class HybridActionSetShareData extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamSetShareData hybridParam = mGson.fromJson(params, HybridParamSetShareData.class);
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
