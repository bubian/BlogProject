package com.pds.blog.web.x5;

import android.app.Activity;
import android.os.Build;
import com.pds.blog.BuildConfig;
import com.pds.blog.web.common.HybridJsInterface;
import com.pds.util.app.BuildVersionUtils;
import com.pds.util.net.NetworkUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.io.File;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 16:19
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class X5Settings {
    public static void initSetting(Activity activity,WebView webView){
        WebSettings settings = webView.getSettings();

        settings.setAppCacheEnabled(true);
        // 需要对H5页面进行缓存，缓存路径及时间未设置
        if (NetworkUtil.isConnected(webView.getContext().getApplicationContext())) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        settings.setAllowFileAccess(true);
        settings.setLayoutAlgorithm(BuildVersionUtils.hasKitkat() ? WebSettings.LayoutAlgorithm.NORMAL : WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
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
        String version = "";
        String user_agent = settings.getUserAgentString()
                .concat(" blog/").concat(BuildConfig.VERSION_NAME)
                .concat("(blog_hb_").concat(BuildConfig.VERSION_NAME.concat(")"))
                .concat("blog_" + version);

        settings.setUserAgentString(user_agent);
        webView.setWebViewClient(new X5WebViewClient());
        webView.setWebChromeClient(new X5WebChromeClient(activity));
        webView.addJavascriptInterface(new HybridJsInterface(), HybridJsInterface.JSInterface);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        //Http和Https混合问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public static void paySettings(WebView webView,String url){
        if (url != null && url.contains("123")) {
            //如果是支付页面或者钱包页面，不缓存。
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
    }
}
