package com.pds.web.action;

import android.content.Context;

import com.pds.web.param.HybridParamUpload;
import com.pds.web.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

public class HybridActionUpload extends HybridAction {
    private static final String TAG = HybridActionUpload.class.getSimpleName();
    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamUpload paramUpload = mGson.fromJson(params, HybridParamUpload.class);
        paramUpload.id = webviewHashCode;
        paramUpload.callback = jsmethod;
        paramUpload.tagname ="uploadImage";
        LogUtil.d(TAG, "onAction param = "+paramUpload);
        EventBus.getDefault().post(paramUpload);
    }
}
