package com.pds.web.action;

import android.content.Context;
import android.os.Bundle;

import com.pds.web.core.HybridConstant;
import com.pds.web.param.HybridParamAnimation;
import com.pds.web.ui.HybridWebViewActivity;
import com.pds.web.util.ActivityUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class HybridActionOpenLink extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            Bundle bundle = new Bundle();
            bundle.putString(HybridConstant.INTENT_EXTRA_KEY_TOPAGE, jsonObject.getString("url"));
            bundle.putSerializable(HybridConstant.INTENT_EXTRA_KEY_ANIMATION, HybridParamAnimation.PUSH);
            bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_HASNAVGATION, true);
            bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_ISNORMAL, true);
            ActivityUtil.toSimpleActivity(context, HybridWebViewActivity.class, HybridParamAnimation.PUSH, bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
