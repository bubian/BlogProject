
package com.pds.web.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pds.web.CommonPath;
import com.pds.web.router.ModuleHybridManager;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;

/**
 * 功能描述
 *
 * @author Jiantao.Yang
 * 2017/2/15 13:10
 * @version 1.0
 */

public class XfiveWebviewCookie {

    private static final String TAG = XfiveWebviewCookie.class.getSimpleName();

    public static boolean cleanCookie(Context context) {
        CookieSyncManager.createInstance(context);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        return false;
    }

    public static boolean initCookie(Context context) {
        String session = ModuleHybridManager.getInstance().getService().getUserSession();
        CookieSyncManager.createInstance(context);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        final String host = CommonPath.getH5CookieDomain();
        String cookie = cookieManager.getCookie(host);
        if (TextUtils.isEmpty(session)) {
            if (!TextUtils.isEmpty(cookie) && cookie.contains("sess=")) {
                String[] split = cookie.split(";");
                int length = split.length;
                final StringBuilder tmpCookie = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    String item = split[i];
                    if (!item.trim().startsWith("sess=")) {
                        tmpCookie.append(item);
                    }
                    if (i < length - 1) {
                        tmpCookie.append(";");
                    }
                }
                Log.e(TAG, "clean before cookie =" + cookie);
                cookieManager.removeAllCookie();
                cookieManager.setCookie(host, tmpCookie.toString());
                CookieSyncManager.getInstance().sync();
                Log.e(TAG, "clean after cookie =" + cookieManager.getCookie(host));
            }
            return false;
        }
        Log.e(TAG, "before cookie =" + cookie);
        cookieManager.setCookie(host, "sess=" + session + ";Domain=." + host + ";Path=/");
        cookieManager.setCookie(host, "platform=app;Domain=." + host + ";Path=/");
        CookieSyncManager.getInstance().sync();
        cookie = cookieManager.getCookie(host);
        Log.e(TAG, "after cookie=" + cookie);
        return true;
    }
}
