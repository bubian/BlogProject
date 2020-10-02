package com.pds.sample.module.patterns;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: Created by pds
 * Date: 2017/10/25
 */

public class ExtWebAgent {
    private WebSettings mWebSettings;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;
    private WebView mWebView;
    private Context mContext;
    private String mJifName;
    private static Map<String, Builder> mCacheBuilder = new HashMap<>();

    private ExtWebAgent(Builder builder, boolean isCache) {
        this(builder);
    }


    public ExtWebAgent(Builder builder) {
        mContext = builder.context;
        mWebView = builder.webView;
        mWebChromeClient = builder.webChromeClient;
        mWebViewClient = builder.webViewClient;
        mJifName = builder.jifName;
        if (null == mWebView) {
            if (null == builder.context) {
                throw new NullPointerException("context is null , can not create webview");
            }
            mWebView = new WebView(mContext);
        }

        if (null == mWebViewClient) {
            mWebViewClient = new WebViewClient();
        }
        mWebView.setWebViewClient(mWebViewClient);
        if (null == mWebChromeClient) {
            mWebChromeClient = new WebChromeClient();
        }
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebSettings = mWebView.getSettings();
    }

    public View web() {
        return mWebView;
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mWebView.loadUrl(url, additionalHttpHeaders);
    }

    public void loadData(String data, String mimeType, String encoding) {
        mWebView.loadData(data, mimeType, encoding);
    }

    public void clearNotDestroy() {
        if (null == mWebView) {
            return;
        }
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
        if (null != viewGroup) {
            viewGroup.removeAllViews();
        }
        mWebView.clearAnimation();
        mWebView.clearHistory();
        mWebView.clearCache(false);

        mWebChromeClient = null;
        mContext = null;
    }

    public void clear() {
        clearNotDestroy();
        mWebView.destroy();
    }

    public void clearHistory(WebView webView) {
        webView = null == webView ? mWebView : webView;
        if (null == webView) {
            return;
        }
        webView.clearHistory();
    }

    public WebView getWebView() {
        return mWebView;
    }

    public WebSettings getWebSettings() {
        return mWebSettings;
    }

    public static class Builder {
        WebViewClient webViewClient;
        WebChromeClient webChromeClient;
        WebView webView;
        Context context;
        String jifName = "native";

        Builder(Context context) {
            if (!(context instanceof Activity || context instanceof Application))
                throw new IllegalArgumentException("context is not Activity or Application");
            this.context = context;
            webChromeClient = new WebChromeClient();
            webViewClient = new WebViewClient();
        }

        public Builder setWebViewClient(WebViewClient wvc) {
            webViewClient = null == wvc ? webViewClient : wvc;
            return this;
        }

        public Builder setChromeClient(WebChromeClient wcc) {
            webChromeClient = null == wcc ? webChromeClient : wcc;
            return this;
        }

        public Builder setWebView(WebView wv) {
            if (wv == null) {
                throw new NullPointerException("setWebView paras is null");
            }
            webView = wv;
            context = wv.getContext();
            return this;
        }

        public ExtWebAgent build() {
            return new ExtWebAgent(this);
        }
    }
}
