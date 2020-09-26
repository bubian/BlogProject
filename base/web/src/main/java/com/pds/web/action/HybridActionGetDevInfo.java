package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamGetDevInfo;
import com.pds.web.router.ModuleHybridManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HybridActionGetDevInfo extends HybridAction {

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamGetDevInfo callback = new HybridParamGetDevInfo();
        callback.id = webviewHashCode;
        callback.callback = jsmethod;
        Map<String, String> devInfos = ModuleHybridManager.getInstance().getService().getNetworkCommonParams();
        if (devInfos != null) {
            try {
                JSONObject object = new JSONObject(devInfos);
                object.put("devNum", devInfos.get("sys_d"));
                callback.callbackData = object;
                EventBus.getDefault().post(callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
