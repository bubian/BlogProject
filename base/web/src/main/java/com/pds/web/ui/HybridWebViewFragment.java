package com.pds.web.ui;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonElement;
import com.pds.web.HybridEventMsg;
import com.pds.web.R;
import com.pds.web.core.HybridAjaxService;
import com.pds.web.core.HybridConfig;
import com.pds.web.core.HybridConstant;
import com.pds.web.param.HybridParamAjax;
import com.pds.web.param.HybridParamBack;
import com.pds.web.param.HybridParamCallback;
import com.pds.web.param.HybridParamDevice;
import com.pds.web.param.HybridParamGetDevInfo;
import com.pds.web.param.HybridParamLog;
import com.pds.web.param.HybridParamPayInfo;
import com.pds.web.param.HybridParamPopup;
import com.pds.web.param.HybridParamRefresh;
import com.pds.web.param.HybridParamScreenshot;
import com.pds.web.param.HybridParamSetShareData;
import com.pds.web.param.HybridParamShare;
import com.pds.web.param.HybridParamShowKeyboard;
import com.pds.web.param.HybridParamShowLoading;
import com.pds.web.param.HybridParamUpdateApp;
import com.pds.web.param.HybridParamUpdateHeader;
import com.pds.web.param.HybridParamUpload;
import com.pds.web.param.HybridParamWebViewHide;
import com.pds.web.param.HybridParamWebViewShow;
import com.pds.web.router.ModuleHybridManager;
import com.pds.web.util.LogUtil;
import com.pds.web.widget.NavgationView;
import com.pds.web.widget.SearchBar;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HybridWebViewFragment extends HybridBaseFragment {
    private static final String TAG = HybridWebViewFragment.class.getSimpleName();

    public static final String ARG_PARAM_URL = "url";
    public static final String ARG_PARAM_FROM = "from";
    public static final String ARG_PARAM_BACK = "back";
    public static final String ARG_PARAM_JUMP = "jump";
    public static final String ARG_PARAM_SHOW_SHADOW = "showShadow";

    private NavgationView mNavgationView;
    private SearchBar mSearchBar;
    private View viewShadow;
    private AlertDialog mDialog;
    private View mDialogView;

    private boolean mHasBack = true;
    private boolean mJump = true;
    private String mUrl;

    protected HybridParamSetShareData mHybridParamSetShareData;
    private HybridParamWebViewShow mHybridParamWebViewShow;
    private HybridParamWebViewHide mHybridParamWebViewHide;

    public HybridWebViewFragment() {
    }

    public static HybridWebViewFragment newInstance(String url) {
        return newInstance(url, null, true, true);
    }

    public static HybridWebViewFragment newInstance(String url, String from, boolean hasHeader, boolean hasBack) {
        HybridWebViewFragment fragment = new HybridWebViewFragment();
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
            mJump = getArguments().getBoolean(ARG_PARAM_JUMP, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i("vane", "mUrl=" + mUrl);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        mNavgationView = view.findViewById(R.id.hybrid_navgation);
        mNavgationView.setCloseIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        viewShadow = view.findViewById(R.id.view_shadow);
        mSearchBar = view.findViewById(R.id.hybrid_searchbar);

        Intent intent = getActivity().getIntent();
        boolean navgation = intent.getBooleanExtra(HybridConstant.INTENT_EXTRA_KEY_HASNAVGATION, true);

        //特殊处理首页
        if (navgation) {
            boolean hasBack = mHasBack && intent.getBooleanExtra(HybridConstant.INTENT_EXTRA_KEY_HASBACK, true);
            if (hasBack) {
                mNavgationView.appendNavgation(NavgationView.Direct.LEFT, "", R.mipmap.icon_naviback_gray, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!onBackPressed()) {
                            getActivity().finish();
                        }
                    }
                }).setVisibility(View.VISIBLE);
                mNavgationView.setTitle(ModuleHybridManager.getInstance().getService().getAppName(getContext()));
            } else {
                mNavgationView.setVisibility(View.VISIBLE);
            }
        } else {
            view.findViewById(R.id.hybrid_header).setVisibility(View.GONE);
        }
        viewShadow.setVisibility(getArguments().getBoolean(ARG_PARAM_SHOW_SHADOW, true) ? View.VISIBLE : View.GONE);
        String userAgent = intent.getStringExtra(HybridConstant.INTENT_EXTRA_KEY_USERAGENT);
        if (!TextUtils.isEmpty(userAgent)) {
            WebSettings settings = mWebView.getSettings();
            settings.setUserAgentString(settings.getUserAgentString() + userAgent);
        }
        mHybridParamWebViewShow = null;
//        if (mJump)
//            onJump(getPageName());
        return view;
    }

    @Override
    protected void initConfig(WebView webView) {
        super.initConfig(webView);
        if (mUrl != null && mUrl.contains(ModuleHybridManager.getInstance().getService().getPayUrl())) {
            //如果是支付页面或者钱包页面，不缓存。
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUrl(mUrl);
    }

    protected String getUrl() {
        Uri data = getActivity().getIntent().getData();
        String url;
        if (null == data) {
            url = getActivity().getIntent().getStringExtra(HybridConstant.INTENT_EXTRA_KEY_TOPAGE);
        } else {
            url = data.toString();
        }
        return url;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEventMainThread(HybridParamWebViewShow msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        LogUtil.i(TAG, String.format("onEventMainThread HybridParamWebViewShow before msg = %s, new  msg= %s", mHybridParamWebViewShow, msg));
        mHybridParamWebViewShow = msg;
    }

    @Subscribe
    public void onEventMainThread(HybridParamWebViewHide msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        mHybridParamWebViewHide = msg;
    }


    public void onWebViewShow() {
        LogUtil.i(TAG, String.format("onWebViewShow viewShowParam = %s", mHybridParamWebViewShow));
        handleHybridCallback(mHybridParamWebViewShow);
    }

    public void onWebViewHide() {
        handleHybridCallback(mHybridParamWebViewHide);
    }

    @Override
    public void onResume() {
        super.onResume();
        onWebViewShow();
    }

    @Override
    public void onPause() {
        super.onPause();
        onWebViewHide();
    }

    /**
     * 返回通知
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(HybridParamBack msg) {
        if (getActivity() == null || null == msg || null == mWebView || msg.id != mWebView.hashCode()) {
            return;
        }
        getActivity().finish();
    }

    void handleHybridCallback(final HybridParamCallback param) {
        LogUtil.i(TAG, String.format("handleHybridCallback param id = %s", (param != null ? param.id : 0)));
        if (null == getActivity() || null == param || null == mWebView || param.id != mWebView.hashCode())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(param.callback)) {
                    String script = "Hybrid.callback(" + new H5RequestEntity(param.callback, param.callbackData != null ? param.callbackData : param.data, param.errno, param.msg).toString() + ")";
                    LogUtil.i(TAG, String.format("handleHybridCallback param id = %s，callback = %s ， data = %s", param.id, script, param.data));
                    mWebView.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            if (!"true".equals(value) && "back".equals(param.tagname))
                                getActivity().finish();
                        }
                    });
                } else if ("back".equals(param.tagname)) {
                    getActivity().finish();
                }
            }
        });
    }


    /**
     * 显示、隐藏loading
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(HybridParamShowLoading msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        if (msg.type.equals(HybridParamShowLoading.TYPE_PROGRESS)) {
            switch (msg.action) {
                case HybridParamShowLoading.ACTION_SHOW:
                    if (mProgessbar.getVisibility() == View.VISIBLE)
                        return;
                    mProgessbar.setVisibility(View.VISIBLE);
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(80);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mProgessbar.setProgress(((int) animation.getAnimatedValue()));
                        }
                    });
                    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    valueAnimator.setDuration(1000);
                    valueAnimator.start();
                    break;

                case HybridParamShowLoading.ACTION_HIDE:
                    if (mProgessbar.getVisibility() != View.VISIBLE)
                        return;
                    mProgessbar.setProgress(100);
                    mWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgessbar.setVisibility(View.GONE);
                            mProgessbar.setProgress(0);
                        }
                    }, 100);
                    break;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(HybridEventMsg<Object> eventMsg) {
        if (null == eventMsg) {
            return;
        }
        switch (eventMsg.getType()) {
            case HybridEventMsg.MEDLINKER_PAY_SUCEED:
                //TODO
//                handleHybridCallback(mHybridParamReward);
//                mHybridParamReward = null;
                break;
            case HybridEventMsg.WEBVIEW_PAGE_LOAD_FINISHED:
                if (eventMsg.getArg1() == mWebView.hashCode()) {
                    mNavgationView.setCloseIconVisible(mWebView.canGoBack());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 支付
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(final HybridParamPayInfo msg) {
        if (null == msg || null == mWebView || msg.id != mWebView.hashCode()) {
            return;
        }
        ModuleHybridManager.getInstance().getService().startPay(msg, new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                HybridParamPayInfo info = (HybridParamPayInfo) arg;
                handleHybridCallback(info);
            }
        });
    }

    @Subscribe
    public void onEventMainThread(final HybridParamDevice param) {
        if (null == param) return;
        if (param.id != mWebView.hashCode()) return;
        param.data = ModuleHybridManager.getInstance().getService().getDeviceId(getContext());
        handleHybridCallback(param);
    }

    @Subscribe
    public void onEventMainThread(final HybridParamUpload param) {
        if (null == param || getActivity() == null) return;
        if (param.id != mWebView.hashCode()) return;
        // 选择图片
        try {
            HybridWebViewActivity activity = (HybridWebViewActivity) getActivity();
            activity.selectPhoto(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventMainThread(final HybridParamLog param) {
        if (null == param || getActivity() == null || param.id != mWebView.hashCode()) {
            return;
        }
        ((HybridWebViewActivity) getActivity()).startLoginActivityForResult(param);
    }

    /**
     * ajax请求
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(final HybridParamAjax msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        if (TextUtils.isEmpty(msg.url)) return;
        if (!msg.url.startsWith("http")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ModuleHybridManager.getInstance().getService().getBaseUrl());
            if (!msg.url.startsWith("/"))
                stringBuilder.append("/");
            stringBuilder.append(msg.url);
            msg.url = stringBuilder.toString();
        }
        Uri uri = Uri.parse(msg.url);
        HybridAjaxService.IApiService service = HybridAjaxService.getService(uri);
        HashMap<String, String> map = ModuleHybridManager.getInstance().getService().getNetworkCommonParams();
        //get参数
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        if (null != queryParameterNames && !queryParameterNames.isEmpty()) {
            Iterator<String> iterator = queryParameterNames.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                map.put(next, uri.getQueryParameter(next));
            }
        }
        //post参数
        if (null != msg.param) {
            Set<Map.Entry<String, JsonElement>> entries = msg.param.entrySet();
            Iterator<Map.Entry<String, JsonElement>> keys = entries.iterator();
            while (keys.hasNext()) {
                Map.Entry<String, JsonElement> next = keys.next();
                map.put(next.getKey(), next.getValue().getAsString());
            }
        }
        String path = uri.getPath();
        Call<String> call = msg.tagname.equals(HybridParamAjax.ACTION.POST) ?
                service.post(path, map) : service.get(path, map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (TextUtils.isEmpty(msg.callback)) return;
                HybridParamCallback hybridParamCallback = new HybridParamCallback();
                hybridParamCallback.callback = msg.callback;
                String body = response.body();
                if (TextUtils.isEmpty(body)) return;
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.has("data")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (null != data) {
                            hybridParamCallback.data = data.toString();
                        } else {
                            hybridParamCallback.data = body;
                        }
                    } else {
                        hybridParamCallback.data = body;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleHybridCallback(hybridParamCallback);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    /**
     * 下拉刷新
     *
     * @param param
     */
    @Subscribe
    public void onEventMainThread(final HybridParamRefresh param) {
        if (null == param) return;
        if (param.id != mWebView.hashCode()) return;
        LogUtil.i(TAG, String.format(" onEventMainThread HybridParamRefresh param id = %s, tagName = %s, data = %s ", param.id, param.tagname, param.data));
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null == param.callback) return;
                mWebView.loadUrl("javascript:Hybrid.callback(" + new H5RequestEntity(param.callback, param.data, param.errno, param.msg).toString() + ")");
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                    }
                }, 1000);
            }
        });

    }


    /**
     * 分享对话框
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(final HybridParamShare msg) {
        if (null == msg || null == mWebView || getActivity() == null) return;
        if (msg.id != mWebView.hashCode()) return;

        ModuleHybridManager.getInstance().getService().startShare(mWebView, msg, new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                HybridParamShare paramShare = (HybridParamShare) arg;
                handleHybridCallback(paramShare);
            }
        });
    }

    /**
     * 更新header通知
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(final HybridParamUpdateHeader msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        if (msg.title.tagname.equals("searchbox")) {
            if (View.VISIBLE == mNavgationView.getVisibility()) {
                mNavgationView.setVisibility(View.GONE);
            }
            if (View.VISIBLE != mSearchBar.getVisibility())
                mSearchBar.setVisibility(View.VISIBLE);
            mSearchBar.cleanSearchbar();
            // left
            ArrayList<HybridParamUpdateHeader.NavgationButtonParam> left = msg.left;
            if (null != left && !left.isEmpty()) {
                int size = left.size();
                for (int i = 0; i < size; i++) {
                    final HybridParamUpdateHeader.NavgationButtonParam param = left.get(i);
                    if (TextUtils.isEmpty(param.icon)) {
                        mSearchBar.appendSearch(SearchBar.Direct.LEFT, param.value, HybridConfig.IconMapping.mapping(param.tagname), new H5UpdateHeaderClickListener(param));
                    } else {
                        mSearchBar.appendSearch(SearchBar.Direct.LEFT, param.value, param.icon, new H5UpdateHeaderClickListener(param));
                    }
                }
            }
            //right
            ArrayList<HybridParamUpdateHeader.NavgationButtonParam> right = msg.right;
            if (null != right && !right.isEmpty()) {
                int size = right.size();
                for (int i = 0; i < size; i++) {
                    final HybridParamUpdateHeader.NavgationButtonParam param = right.get(i);
                    if (TextUtils.isEmpty(param.icon)) {
                        mSearchBar.appendSearch(SearchBar.Direct.RIGHT, param.value, HybridConfig.IconMapping.mapping(param.tagname), new H5UpdateHeaderClickListener(param));
                    } else {
                        mSearchBar.appendSearch(SearchBar.Direct.RIGHT, param.value, param.icon, new H5UpdateHeaderClickListener(param));
                    }
                }
            }
            if (null != msg.title.callback) {
                mSearchBar.setData(msg.title.focus, msg.title.placeholder, msg.style.backgroundcolor, new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        HybridParamCallback callback = new HybridParamCallback();
                        callback.callback = msg.title.callback;
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("type", hasFocus ? "editingdidbegin" : "editingdidend");
                            String text = ((EditText) v).getText().toString().trim();
                            jsonObject.put("data", text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.data = jsonObject.toString();
                        handleHybridCallback(callback);
                    }
                }, new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        HybridParamCallback callback = new HybridParamCallback();
                        callback.callback = msg.title.callback;
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("type", "editingchanged");
                            jsonObject.put("data", s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.data = jsonObject.toString();
                        handleHybridCallback(callback);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                mSearchBar.setData(msg.title.focus, msg.title.placeholder, msg.style.backgroundcolor, null, null);
            }
        } else {
            if (View.VISIBLE != mNavgationView.getVisibility()) {
                mNavgationView.setVisibility(View.VISIBLE);
            }
            if (View.VISIBLE == mSearchBar.getVisibility())
                mSearchBar.setVisibility(View.GONE);
            mNavgationView.cleanNavgation();
            // left
            ArrayList<HybridParamUpdateHeader.NavgationButtonParam> left = msg.left;
            if (null != left && !left.isEmpty()) {
                int size = left.size();
                for (int i = 0; i < size; i++) {
                    final HybridParamUpdateHeader.NavgationButtonParam param = left.get(i);
                    if (TextUtils.isEmpty(param.icon)) {
                        mNavgationView.appendNavgation(NavgationView.Direct.LEFT, param.value, HybridConfig.IconMapping.mapping(param.tagname), new H5UpdateHeaderClickListener(param));
                    } else {
                        mNavgationView.appendNavgation(NavgationView.Direct.LEFT, param.value, param.icon, new H5UpdateHeaderClickListener(param));
                    }
                }
            }
            //right
            ArrayList<HybridParamUpdateHeader.NavgationButtonParam> right = msg.right;
            if (null != right && !right.isEmpty()) {
                int size = right.size();
                for (int i = 0; i < size; i++) {
                    final HybridParamUpdateHeader.NavgationButtonParam param = right.get(i);
                    if (TextUtils.isEmpty(param.icon)) {
                        mNavgationView.appendNavgation(NavgationView.Direct.RIGHT, param.value, HybridConfig.IconMapping.mapping(param.tagname), new H5UpdateHeaderClickListener(param));
                    } else {
                        mNavgationView.appendNavgation(NavgationView.Direct.RIGHT, param.value, param.icon, new H5UpdateHeaderClickListener(param));
                    }
                }
            }
            //title
            HybridParamUpdateHeader.NavgationTitleParam title = msg.title;
            mNavgationView.setTitle(title.title, title.subtitle, title.lefticon, title.righticon, new H5UpdateHeaderClickListener(title));
        }
    }

    @Subscribe
    public void onEventMainThread(HybridParamSetShareData msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        mHybridParamSetShareData = msg;
    }

    @Subscribe
    public void onEventMainThread(HybridParamPopup msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        showDialog(msg);
    }

    private void showDialog(final HybridParamPopup msg) {
        if (null == mDialog) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mDialogView = inflater.inflate(R.layout.hybrid_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(mDialogView);
            mDialog = builder.create();
            mDialogView.findViewById(R.id.hybrid_dialog_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
        }
        final ImageView imageView = mDialogView.findViewById(R.id.hybrid_dialog_img);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(layoutParams);

        imageView.setMaxWidth(ModuleHybridManager.getInstance().getService().getScreenWidth());
        imageView.setMaxHeight(ModuleHybridManager.getInstance().getService().getScreenHeight());

        Glide.with(this).load(msg.imgUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isDetached() || null == getContext())
                                    return;
                                mDialog.show();
                            }
                        }, 800);
                        return false;
                    }
                })
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                handleHybridCallback(msg);
            }
        });
    }

    @Subscribe
    public void onEventMainThread(HybridParamGetDevInfo callback) {
        if (null == callback || null == mWebView || getActivity() == null) return;
        if (callback.id != mWebView.hashCode()) return;
        handleHybridCallback(callback);
    }

    @Subscribe
    public void onEventMainThread(HybridParamShowKeyboard.HybridParamShowKeyboardCallback callback) {
        if (null == callback || null == mWebView || getActivity() == null) return;
        if (callback.id != mWebView.hashCode()) return;
        handleHybridCallback(callback);
    }

    @Subscribe
    public void onEventMainThread(HybridParamShowKeyboard.HybridParamUploadCallback callback) {
        if (null == callback || null == mWebView || getActivity() == null) return;
        if (callback.id != mWebView.hashCode()) return;
        handleHybridCallback(callback);
    }

    /**
     * 截屏
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(HybridParamScreenshot msg) {
        if (null == msg || null == mWebView || getActivity() == null) return;
        if (msg.id != mWebView.hashCode()) return;
        ModuleHybridManager.getInstance().getService().saveImgToLocal(mWebView.getContext().getApplicationContext(), mWebView, true);
    }

    @Subscribe
    public void onEventMainThread(HybridParamUpdateApp msg) {
        if (null == msg || null == mWebView) return;
        if (msg.id != mWebView.hashCode()) return;
        ModuleHybridManager.getInstance().getService().toAppStore(getContext());
    }

    private final class H5UpdateHeaderClickListener implements View.OnClickListener {

        private HybridParamCallback param;

        public H5UpdateHeaderClickListener(HybridParamCallback param) {
            this.param = param;
        }

        @Override
        public void onClick(View v) {
            handleHybridCallback(param);
        }
    }

    public static final class H5RequestEntity {
        public String callback;
        public Object data;
        public int errno;
        public String msg;

        public H5RequestEntity(String callback, Object data, int errno, String msg) {
            this.data = data;
            this.callback = callback;
            this.errno = errno;
            this.msg = msg;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("callback", callback);
                jsonObject.put("data", data);
                jsonObject.put("errno", errno);
                jsonObject.put("msg", msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

    @Override
    public boolean onBackPressed() {
        boolean result = false;
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            result = true;
        }
        return result;
    }
}
