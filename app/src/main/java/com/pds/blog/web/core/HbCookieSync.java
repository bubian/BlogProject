
package com.pds.blog.web.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.pds.blog.web.common.HbPath;

/**
 * com.tencent.smtt.sdk.CookieManager和com.tencent.smtt.sdk.CookieSyncManager的相关接口的调用，
 * ®在接入SDK后，需要放到创建X5的WebView之后（也就是X5内核加载完成）进行；否则，cookie的相关操作只能影响系统内核。
 */
public class HbCookieSync {

    private static final String TAG = HbCookieSync.class.getSimpleName();


    public static boolean cleanCookie(Context context) {
        CookieSyncManager.createInstance(context);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        return false;
    }

    public static boolean initCookie(Context context) {
        String session = HbCookieHelper.instance().getSession();
        CookieSyncManager.createInstance(context);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        final String host = HbPath.getH5CookieDomain();
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
