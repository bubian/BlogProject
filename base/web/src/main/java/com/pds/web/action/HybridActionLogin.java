package com.pds.web.action;

import android.content.Context;
import android.os.Bundle;

import com.pds.web.param.HybridParamLog;

import org.greenrobot.eventbus.EventBus;


public class HybridActionLogin extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamLog hybridParam = null;
        if (null == params) {
            hybridParam = new HybridParamLog();
        } else {
            hybridParam = mGson.fromJson(params, HybridParamLog.class);
        }
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        hybridParam.tagname = "login";
        Bundle data = new Bundle();
        data.putString("med_project", hybridParam.getMed_project());
        data.putString("med_channel", hybridParam.getMed_channel());
        EventBus.getDefault().post(hybridParam);
    }
}
