package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamAjax;

import org.greenrobot.eventbus.EventBus;

public class HybridActionAjaxPost extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamAjax hybridParam = mGson.fromJson(params, HybridParamAjax.class);
        hybridParam.tagname = HybridParamAjax.ACTION.POST;
        hybridParam.callback = jsmethod;
        hybridParam.id = webviewHashCode;
        EventBus.getDefault().post(hybridParam);
    }
}
