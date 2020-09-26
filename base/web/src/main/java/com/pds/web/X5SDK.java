package com.pds.web;

import android.content.Context;

import com.pds.web.core.XfiveWebviewCookie;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

public class X5SDK {

    public static void init(Context context) {
        XfiveWebviewCookie.initCookie(context);
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.initX5Environment(context, null);
    }
}
