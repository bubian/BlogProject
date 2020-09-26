package com.pds.web.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.blog.pds.net.BuildConfig;
import com.pds.web.CommonPath;
import com.pds.web.HybridEventMsg;
import com.pds.web.action.HybridAction;
import com.pds.web.router.ModuleHybridManager;
import com.pds.web.util.FileUtil;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by vane on 16/6/2.
 */

public class HyBridWebViewClient extends WebViewClient {

    private static final String TAG = HyBridWebViewClient.class.getSimpleName();

    static final int REQUEST_INTERVAL_TIME = 200;
    long lastOverrideTime = 0L;
    String lastOverrideMethodStr = null;

    public static boolean useCache = true;

    public HyBridWebViewClient() {
        setHostFilter(CommonPath.getH5Host());
    }

    private String mSubHost;

    void setHostFilter(String schemeAndHost) {
        mSubHost = Uri.parse(schemeAndHost).getHost();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format(" shouldInterceptRequest view url = %s, cacheState = %b, subHost = %s ", url, useCache, mSubHost));
        }
        // 需要重新讨论协议标准,建议url路径和本地的压缩包目录结构相同
        if (!useCache || TextUtils.isEmpty(mSubHost) || TextUtils.isEmpty(url) || !url.contains(mSubHost))
            return super.shouldInterceptRequest(view, url);
        String tempUrl = url.replace("/h5", "");
        //-----------------------------------------
        Uri uri = Uri.parse(tempUrl);
        if (TextUtils.isEmpty(uri.getPath())) return super.shouldInterceptRequest(view, url);
        File file = new File(FileUtil.getRootDir(view.getContext()).getAbsolutePath() + "/" + HybridConfig.FILE_HYBRID_DATA_PATH + uri.getPath());
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format(" shouldInterceptRequest filepath = %s， fileExist = %b", file, file.exists()));
        }
        if (file.exists()) {
            WebResourceResponse response = null;
            try {
                InputStream localCopy = new FileInputStream(file);
                String mimeType = getMimeType(tempUrl);
                response = new WebResourceResponse(mimeType, "UTF-8", localCopy);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
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
        if (HybridConfig.SCHEME.equals(scheme)) {
            String host = parse.getHost();
            String param = parse.getQueryParameter(HybridConstant.GET_PARAM);
            String callback = parse.getQueryParameter(HybridConstant.GET_CALLBACK);
            if (null == HybridConfig.TagnameMapping.mapping(host)) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            try {
                hybridDispatcher(view, host, param, callback);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return false;
        } else if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            Context context = view.getContext();
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        } else if (url.startsWith("alipays:") || url.startsWith("alipay")) {
            if (isPaySuccess) {
                int payBeforeIndex = view.copyBackForwardList().getCurrentIndex();
                if (payBeforeIndex > 1) {
                    view.goBackOrForward(-payBeforeIndex);
                }
                isPaySuccess = false;
                return true;
            }
            final Activity activity = ModuleHybridManager.getInstance().getService().getCurrentActivity();
            try {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                isPaySuccess = true;
            } catch (Exception e) {
                isPaySuccess = false;
                ModuleHybridManager.getInstance().getService().payInterceptorWithUrl(url, new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        // 支付结果返回
                        final String url = (String) arg;
                        if (!TextUtils.isEmpty(url)) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(url);
                                }
                            });
                        }
                    }
                });
            }
            return true;
        } else if (url.startsWith("weixin://wap/pay")) {
            try {
                final Activity activity = ModuleHybridManager.getInstance().getService().getCurrentActivity();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (Exception e) {
                ModuleHybridManager.getInstance().getService().showToastMessage("无法调起微信支付");
                e.printStackTrace();
            }
            return true;
        } else if (url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb")) {
            Map<String, String> header = new HashMap<>();
            header.put("Referer", "https://activity.m.duiba.com.cn");
            view.loadUrl(url, header);
            return false;
        }

//
//        else if (url.contains("activity.m.duiba.com.cn/crecord/recordDetailNew")){
//            int payBeforeIndex = view.copyBackForwardList().getCurrentIndex();
//            if (payBeforeIndex > 1){
//                view.goBackOrForward(-payBeforeIndex);
//            }
//        }
        view.loadUrl(url);
        return false;
    }

    @Override
    public void onPageFinished(WebView webView, String s) {
        super.onPageFinished(webView, s);
        EventBus.getDefault().post(new HybridEventMsg<>(HybridEventMsg.WEBVIEW_PAGE_LOAD_FINISHED, 0, webView.hashCode()));
    }

    private void hybridDispatcher(final WebView webView, String method, String params, String jsmethod) throws IllegalAccessException, InstantiationException {
        if (!method.equals(lastOverrideMethodStr) || (System.currentTimeMillis() - lastOverrideTime > REQUEST_INTERVAL_TIME)) {
            Class type = HybridConfig.TagnameMapping.mapping(method);
            HybridAction action = (HybridAction) type.newInstance();
            action.onAction(webView.getContext(), webView.hashCode(), params, jsmethod);
        }
        lastOverrideTime = System.currentTimeMillis();
        lastOverrideMethodStr = method;
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
