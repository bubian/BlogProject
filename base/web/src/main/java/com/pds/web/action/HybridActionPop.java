package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamPop;
import com.pds.web.router.ModuleHybridManager;
import com.pds.web.ui.HybridWebViewActivity;

public class HybridActionPop extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamPop hybridParam = mGson.fromJson(params, HybridParamPop.class);
        if (hybridParam != null && hybridParam.getNum() >= 0) {
//            LogUtil.i("HybridActionPop", " onAction num = %d ",hybridParam.getNum());
            ModuleHybridManager.getInstance().getService().pop(hybridParam.getNum(), HybridWebViewActivity.class);
        }

    }
}
