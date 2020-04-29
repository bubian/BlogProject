package com.pds.blog.web.core;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 10:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HbCookieHelper {

    private static final String TAG = "HbCookieHelper";
    private static HbCookieHelper cookieHelper = new HbCookieHelper();

    private String session;

    public static HbCookieHelper instance(){
        return cookieHelper;
    }


    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    /**
     * 判断是否需要替换本地sess
     */
    public String checkSession(String url) {
        Log.d(TAG, "checkSession start mWebUrl = " + url);
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        Uri uri = Uri.parse(url);
        String sess = uri.getQueryParameter("sess");
        if (!TextUtils.isEmpty(sess)) {
            String userSess = session;
            url = url.replace("sess=sess", "sess=".concat(userSess));
        }
        Log.d(TAG, "checkSession end mWebUrl = " + url);
        return url;
    }
}
