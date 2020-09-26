package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamScreenshot;

import org.greenrobot.eventbus.EventBus;

/**
 * 截屏
 * Created by jiantao on 2017/3/14.
 */
public class HybridActionedScreenshot extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamScreenshot hybridParam = mGson.fromJson(params, HybridParamScreenshot.class);
        hybridParam.id = webviewHashCode;
        hybridParam.callback = jsmethod;
        EventBus.getDefault().post(hybridParam);
    }
}
