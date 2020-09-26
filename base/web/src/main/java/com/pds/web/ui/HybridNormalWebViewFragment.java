package com.pds.web.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pds.web.R;
import com.pds.web.param.HybridParamSetShareData;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.Subscribe;


/**
 * Created by vane on 16/6/3.
 */
public class HybridNormalWebViewFragment extends HybridWebViewFragment implements View.OnClickListener {

    private ImageView hybridNormalBack;
    private TextView hybridNormalClose;
    private TextView hybridNormalTitle;
    private ImageView hybridNormalShare;

    public HybridNormalWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initConfig(WebView webView) {
        super.initConfig(webView);
        setExtenalWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                hybridNormalTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    public static HybridNormalWebViewFragment newInstance(String url) {
        HybridNormalWebViewFragment fragment = new HybridNormalWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.hybrid_normal_webview, (ViewGroup) view.findViewById(R.id.hybrid_header), true);
        view.findViewById(R.id.hybrid_navgation).setVisibility(View.GONE);


        hybridNormalBack = view.findViewById(R.id.hybrid_normal_back);
        hybridNormalClose = view.findViewById(R.id.hybrid_normal_close);
        hybridNormalTitle = view.findViewById(R.id.hybrid_normal_title);
        hybridNormalShare = view.findViewById(R.id.hybrid_normal_share);
        hybridNormalBack.setOnClickListener(this);
        hybridNormalClose.setOnClickListener(this);
        hybridNormalShare.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.hybrid_normal_back) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else if (null != getActivity()) {
                getActivity().finish();
            }
        } else if (id == R.id.hybrid_normal_close) {
            if (null != getActivity()) {
                getActivity().finish();
            }
        } else if (id == R.id.hybrid_normal_share) {
            onEventMainThread(mHybridParamSetShareData.cover());
        }
    }

    @Subscribe
    public void onEventMainThread(HybridParamSetShareData msg) {
        super.onEventMainThread(msg);
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        hybridNormalShare.setVisibility(View.VISIBLE);
    }
}
