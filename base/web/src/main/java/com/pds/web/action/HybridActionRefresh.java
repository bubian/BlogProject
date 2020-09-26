package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamRefresh;

import org.greenrobot.eventbus.EventBus;


public class HybridActionRefresh extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamRefresh hybridParam = mGson.fromJson(params, HybridParamRefresh.class);
        hybridParam.callback = jsmethod;
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
