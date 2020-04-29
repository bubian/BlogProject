package com.pds.blog.web.x5;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.pds.blog.BuildConfig;
import com.pds.blog.web.common.HbPath;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 16:27
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class X5WebViewClient extends WebViewClient {

    private static final String TAG = X5WebViewClient.class.getSimpleName();

    public static boolean useCache = true;

    public X5WebViewClient() {
        setHostFilter(HbPath.getH5Host());
    }

    private String mSubHost;

    void setHostFilter(String schemeAndHost) {
        mSubHost = Uri.parse(schemeAndHost).getHost();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("shouldInterceptRequest view url = %s, cacheState = %b, subHost = %s ", url, useCache, mSubHost));
        }
        // 需要重新讨论协议标准,建议url路径和本地的压缩包目录结构相同
        if (!useCache || TextUtils.isEmpty(mSubHost) || TextUtils.isEmpty(url) || !url.contains(mSubHost)) {
            return super.shouldInterceptRequest(view, url);
        }
        return super.shouldInterceptRequest(view, url);
    }

    private volatile boolean isPaySuccess = false;

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        Uri parse = Uri.parse(url);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "shouldOverrideUrlLoading url=" + url);
        }
        String scheme = parse.getScheme();
        view.loadUrl(url);
        return false;
    }

    @Override
    public void onPageFinished(WebView webView, String s) {
        super.onPageFinished(webView, s);
    }

    private String getMimeType(String url) {
        try {
            if (url.contains(".")) {
                int index = url.lastIndexOf(".");
                if (index > -1) {
                    int paramIndex = url.indexOf("?");
                    String type = url.substring(index + 1, paramIndex == -1 ? url.length() : paramIndex);
                    switch (type) {
                        case "js":
                            return "text/javascript";
                        case "css":
                            return "text/css";
                        case "html":
                            return "text/html";
                        case "png":
                            return "image/png";
                        case "jpg":
                            return "image/jpg";
                        case "gif":
                            return "image/gif";
                        default:
                    }
                }
            }
            return "text/plain";
        } catch (Exception e) {
            e.printStackTrace();
            return "text/plain";
        }
    }
}
