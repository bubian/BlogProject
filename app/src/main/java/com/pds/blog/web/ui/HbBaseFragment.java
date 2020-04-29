package com.pds.blog.web.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pds.blog.R;
import com.pds.blog.web.core.HbCookieHelper;
import com.pds.blog.web.x5.X5Settings;
import com.pds.blog.web.widget.HybridWebView;
import com.pds.ui.view.refresh.MultipleSwipeRefreshLayout;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 13:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HbBaseFragment extends Fragment {
    private static final String TAG = "HbBaseFragment";
    protected WebView mHbWebView;
    private MultipleSwipeRefreshLayout mRefreshLayout;
    protected ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hb_webview_act, null);
        mRefreshLayout = view.findViewById(R.id.hb_refresh);
        mRefreshLayout.setEnabled(false);
        mHbWebView = createWebView(inflater);
        mRefreshLayout.addView(mHbWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mProgressBar = view.findViewById(R.id.hb_progress_bar);
        X5Settings.initSetting(getActivity(),mHbWebView);
        return view;
    }

    public WebView createWebView(LayoutInflater inflater) {
        return new HybridWebView(inflater.getContext());
    }

    protected void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) return;
        Log.i(TAG, " load url  = " + url);
        mHbWebView.loadUrl(HbCookieHelper.instance().checkSession(url));
    }

    @Override
    public void onResume() {
        super.onResume();
        mHbWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHbWebView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mHbWebView) {
            changeVideoState(false);
            mHbWebView.removeAllViews();
            ((ViewGroup) mHbWebView.getParent()).removeView(mHbWebView);
//            mWebView.destroy();
            mHbWebView = null;
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
            mHbWebView.evaluateJavascript(script, null);
            mHbWebView.evaluateJavascript(handle, null);
        } else {
            mHbWebView.loadUrl("javascript:" + script);
            mHbWebView.loadUrl("javascript:" + handle);
        }
    }

    public boolean onBackPressed() {
        boolean result = false;
        if (mHbWebView.canGoBack()) {
            mHbWebView.goBack();
            result = true;
        }
        return result;
    }
}
