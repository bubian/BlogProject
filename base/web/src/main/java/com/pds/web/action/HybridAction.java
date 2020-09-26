package com.pds.web.action;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pds.web.param.HybridParamAnimation;
import com.pds.web.param.HybridParamType;

public abstract class HybridAction {
    public static Gson mGson;

    static {
        mGson = new GsonBuilder()
                .registerTypeAdapter(HybridParamAnimation.class, new HybridParamAnimation.TypeDeserializer())
                .registerTypeAdapter(HybridParamType.class, new HybridParamType.TypeDeserializer())
                .create();
    }

    //    public abstract void onAction(WebView webView, String params, String jsmethod);
    public abstract void onAction(Context context, int webviewHashCode, String params, String jsmethod);

}
