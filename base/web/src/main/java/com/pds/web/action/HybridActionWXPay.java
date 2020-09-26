package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamPayInfo;

import org.greenrobot.eventbus.EventBus;

public class HybridActionWXPay extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamPayInfo hybridParam = mGson.fromJson(params, HybridParamPayInfo.class);
        hybridParam.id = webviewHashCode;
        hybridParam.tagname = "paybywxpay";
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
