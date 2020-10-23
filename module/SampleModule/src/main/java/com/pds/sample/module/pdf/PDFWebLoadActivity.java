package com.pds.sample.module.pdf;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.base.act.BaseActivity;
import com.pds.sample.R;
import com.pds.sample.router.ARouterPath;

import java.io.File;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/20 2:46 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = ARouterPath.WEB_PDF)
public class PDFWebLoadActivity extends BaseActivity {

    private static final String PDF_URL = "http://47.104.91.148/web/guanxin_paitent_test.pdf";

    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pdf);
        mWebView = findViewById(R.id.web_view);
        initSetting(mWebView);
        mWebView.loadUrl(buildUrlLocal(PDF_URL));
    }

    protected void initSetting(WebView webView) {
        WebSettings settings = webView.getSettings();

        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLayoutAlgorithm(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ? WebSettings.LayoutAlgorithm.NORMAL : WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadWithOverviewMode(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setAppCachePath(new File(webView.getContext().getCacheDir(), "wb_appcache").getAbsolutePath());
        settings.setDatabasePath(new File(webView.getContext().getCacheDir(), "wb_databases").getAbsolutePath());
        settings.setGeolocationDatabasePath(new File(webView.getContext().getCacheDir(), "wb_geolocation").getAbsolutePath());
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // settings.setUserAgentString("");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //方便webview在choreme调试
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //Http和Https混合问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * 通过网页导入PDF文件
     * <p>
     * 参考：https://github.com/mozilla/pdf.js
     *
     * @param view
     */
    public void doWebLoadPdf(View view) {

    }

    private String buildUrl(String pdfUrl) {
        return getPdfLink(true) + "?file=" + pdfUrl;
    }

    private String buildUrlLocal(String pdfUrl) {
        return getPdfLinkLocal() + "?file=" + pdfUrl;
    }

    /**
     * Android10访问被拒绝
     * @param isOld
     * @return
     */
    private String getPdfLink(boolean isOld) {
        return isOld ? "https://mozilla.github.io/pdf.js/es5/web/viewer.html" : "https://mozilla.github.io/pdf.js/web/viewer.html";
    }

    private String getPdfLinkLocal() {
        return "file:///android_asset/pdfweb/web/viewer.html";
    }
}
