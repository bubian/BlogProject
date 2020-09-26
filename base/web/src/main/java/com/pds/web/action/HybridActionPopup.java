package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamPopup;

import org.greenrobot.eventbus.EventBus;


public class HybridActionPopup extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamPopup hybridParam = mGson.fromJson(params, HybridParamPopup.class);
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        hybridParam.tagname = "activitypopup";
        EventBus.getDefault().post(hybridParam);
    }
}
