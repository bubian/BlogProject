package com.pds.web.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pds.web.R;
import com.pds.web.core.HyBridWebViewClient;
import com.pds.web.core.HybridConfig;
import com.pds.web.core.HybridJsInterface;
import com.pds.web.router.ModuleHybridManager;
import com.pds.web.util.LogUtil;
import com.pds.web.util.NetworkUtil;
import com.pds.web.util.UserAgentUtil;
import com.pds.web.widget.HybridWebView;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HybridBaseFragment extends Fragment {
    private static final String TAG = HybridBaseFragment.class.getSimpleName();

    protected WebView mWebView;
    private HybridWebChromeClient mWebChromeClient;
    protected ProgressBar mProgessbar;

    protected SmartRefreshLayout mRefreshLayout;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hybrid_webview, null);
        mRefreshLayout = view.findViewById(R.id.layout_refresh);
        mRefreshLayout.setRefreshHeader(new MaterialHeader(container.getContext()));
        mRefreshLayout.setEnabled(false);
        mWebView = createWebView(inflater);
        mRefreshLayout.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mProgessbar = view.findViewById(R.id.hybrid_progressbar);
        initConfig(mWebView);
        return view;
    }

    //默认使用HyrbidWebView
    protected WebView createWebView(LayoutInflater inflater) {
        return new HybridWebView(inflater.getContext());
    }

    /**
     * 需要设置webview的属性则重写此方法
     *
     * @param webView
     */
    protected void initConfig(WebView webView) {
        WebSettings settings = webView.getSettings();

        settings.setAppCacheEnabled(true);
        // 需要对H5页面进行缓存，缓存路径及时间未设置
        if (NetworkUtil.isConnected(webView.getContext().getApplicationContext())) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
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
        settings.setUserAgentString(UserAgentUtil.getMedUserAgent(settings.getUserAgentString()));
        webView.setWebViewClient(new HyBridWebViewClient());
        mWebChromeClient = new HybridWebChromeClient(getActivity(), HybridWebViewActivity.FILECHOOSER_RESULTCODE);
        webView.setWebChromeClient(mWebChromeClient);
        webView.addJavascriptInterface(new HybridJsInterface(), HybridConfig.JSInterface);
        HashMap<String, Object> jsInterfaceMap = ModuleHybridManager.getInstance().getService()
                .getJsInterface(mWebView, getActivity());
        for (Map.Entry<String, Object> entry : jsInterfaceMap.entrySet()) {
            webView.addJavascriptInterface(entry.getValue(), entry.getKey());
        }
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

    protected void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) return;
        LogUtil.i(TAG, " loadurl url = " + url);
        mWebView.loadUrl(checkSession(url));
    }


    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mWebView) {
            changeVideoState(false);
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
//            mWebView.destroy();
            mWebView = null;
        }
        ModuleHybridManager.getInstance().getService().onDestroyHybrid();
    }

    private boolean mIntercepterVisibleHint;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        try {
            super.setUserVisibleHint(isVisibleToUser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mIntercepterVisibleHint && this.getView() != null)
                this.getView().setVisibility(isVisibleToUser ? View.VISIBLE : View.GONE);
        }
    }

    public void setIntercepterVisibleHint(boolean intercepterVisibleHint) {
        mIntercepterVisibleHint = intercepterVisibleHint;
    }

    public void setExtenalWebChromeClient(WebChromeClient webChromeClient) {
        if (null != mWebChromeClient) {
            mWebChromeClient.setExtenalWebChromeClient(webChromeClient);
        } else {
            mWebView.setWebChromeClient(webChromeClient);
        }
    }

    /**
     * 判断是否需要替换本地sess
     */
    private String checkSession(String url) {
//        Logger.d(TAG, "checkSession start", "mWebUrl = " + url);
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        Uri uri = Uri.parse(url);
        String sess = uri.getQueryParameter("sess");
        if (!TextUtils.isEmpty(sess)) {
            String userSess = ModuleHybridManager.getInstance().getService().getUserSession();
            url = url.replace("sess=sess", "sess=".concat(userSess));
        }
//        Logger.d(TAG, "checkSession end", "mWebUrl = " + url);
        return url;
    }

    public static ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> uploadMessageAboveL;

    public boolean onBackPressed() {
        return false;
    }

    public class HybridWebChromeClient extends WebChromeClient {
        private Activity mActivity;
        private final int mRequestCode;
        private WebChromeClient mChromeClient;

        public HybridWebChromeClient(Activity activity, int requestCode) {
            mActivity = activity;
            mRequestCode = requestCode;
        }

        public void setExtenalWebChromeClient(WebChromeClient webChromeClient) {
            mChromeClient = webChromeClient;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            boolean showProgress = newProgress < 100;
            mProgessbar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
            mProgessbar.setProgress(newProgress);
            if (null != mChromeClient) mChromeClient.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (null != mChromeClient)
                mChromeClient.onReceivedTitle(view, title);
        }

        //For Android 4.1
        @Override
        public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUploadMessage = uploadMsg;
                    picker();
                }
            });

        }

        @Override
        public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadMessageAboveL = filePathCallback;
                    picker();
                }
            });
            return true;
        }

        private void picker() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mActivity.startActivityForResult(intent, mRequestCode);
            } else {
                mActivity.startActivityForResult(Intent.createChooser(intent, "图片选择"), mRequestCode);
            }
        }
    }

    public void changeVideoState(boolean play) {
        String script = "function changeVideoState(play){\n" +
                "   var audio= document.getElementsByTagName(\"audio\");\n" +
                "   if(audio.length>0){\n" +
                "      for (var i = 0;i<audio.length;i++){\n" +
                "          if (play){\n" +
                "              audio[i].play();\n" +
                "          }else{\n" +
                "              audio[i].pause();\n" +
                "          }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String handle = "changeVideoState(" + play + ")";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, null);
            mWebView.evaluateJavascript(handle, null);
        } else {
            mWebView.loadUrl("javascript:" + script);
            mWebView.loadUrl("javascript:" + handle);
        }
    }

    /**
     * 获取当前webClient加载的网页URL地址
     *
     * @return
     */
    public String getCurrentUrl() {
        return mWebView.getUrl();
    }

}
