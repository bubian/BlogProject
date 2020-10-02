package com.pds.web.x5;

import android.content.Context;

import java.util.LinkedHashMap;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 11:06
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class X5CrashInfo {

    public static LinkedHashMap<String, String> initX5CrashUpload(Context appContext){
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(appContext);
        map.put("x5crashInfo", x5CrashInfo);
        return map;
    }
}
