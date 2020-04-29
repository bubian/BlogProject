package com.pds.blog.web.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.pds.blog.R;
import com.pds.blog.web.common.HbC;
import com.pds.blog.web.widget.NavigationView;
import com.pds.blog.web.widget.SearchBar;
import com.pds.blog.web.x5.X5Settings;
import com.tencent.smtt.sdk.WebSettings;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 13:28
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HbFragment extends HbMiddleFragment {

    public static final String ARG_PARAM_URL = "url";
    public static final String ARG_PARAM_FROM = "from";
    public static final String ARG_PARAM_BACK = "back";
    public static final String ARG_PARAM_JUMP = "jump";
    public static final String ARG_PARAM_SHOW_SHADOW = "showShadow";
    public static final String TYPE_IMAGE = "image";

    private boolean mHasBack = true;
    private String mUrl;

    private NavigationView mNavigationView;
    private SearchBar mSearchBar;
    private View viewShadow;

    public HbFragment() {}

    public static HbFragment newInstance(String url) {
        return newInstance(url, null, true, true);
    }

    public static HbFragment newInstance(String url, String from, boolean hasHeader, boolean hasBack) {
        HbFragment fragment = new HbFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_URL, url);
        args.putString(ARG_PARAM_FROM, from);
        args.putBoolean(ARG_PARAM_BACK, hasBack);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_PARAM_URL);
            mHasBack = getArguments().getBoolean(ARG_PARAM_BACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mNavigationView = view.findViewById(R.id.hb_navigation);
        mNavigationView.setCloseIconOnClickListener(v -> getActivity().finish());
        viewShadow = view.findViewById(R.id.hb_shadow);
        mSearchBar = view.findViewById(R.id.hb_search_bar);

        Intent intent = getActivity().getIntent();
        boolean navigation = intent.getBooleanExtra(HbC.INTENT_EXTRA_KEY_HASNAVGATION, true);
        //特殊处理首页
        if (navigation) {
            boolean hasBack = mHasBack && intent.getBooleanExtra(HbC.INTENT_EXTRA_KEY_HASBACK, true);
            if (hasBack) {
                mNavigationView.appendNavgation(NavigationView.Direct.LEFT, "", R.mipmap.icon_nav_back_gray, (View.OnClickListener) v -> {
                    if (!onBackPressed()) {
                        getActivity().finish();
                    }
                }).setVisibility(View.VISIBLE);
                mNavigationView.setTitle(getString(R.string.app_name));
            } else {
                mNavigationView.setVisibility(View.VISIBLE);
            }
        } else {
            view.findViewById(R.id.hb_header).setVisibility(View.GONE);
        }
        viewShadow.setVisibility(getArguments().getBoolean(ARG_PARAM_SHOW_SHADOW, true) ? View.VISIBLE : View.GONE);
        String userAgent = intent.getStringExtra(HbC.INTENT_EXTRA_KEY_USERAGENT);
        if (!TextUtils.isEmpty(userAgent)) {
            WebSettings settings = mHbWebView.getSettings();
            settings.setUserAgentString(settings.getUserAgentString() + userAgent);
        }
        // 支付设置
        X5Settings.paySettings(mHbWebView,mUrl);
        return view;
    }

}
