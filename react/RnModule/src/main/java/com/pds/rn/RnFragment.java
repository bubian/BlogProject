package com.pds.rn;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.pds.rn.rectfragment.BaseReactFragment;
import com.pds.rn.rectfragment.BaseReactFragmentDelegate;
import com.pds.rn.rectfragment.ReactFragmentDelegate;
import com.pds.rn.utils.FragmentRNCollector;
import com.pds.rn.utils.RnEventHelper;
import com.pds.router.module.BundleKey;
import com.pds.router.module.ModuleGroupRouter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Author: pengdaosong.
 * <p>
 * CreateTime:  2018/11/9 4:10 PM
 * <p>
 * Email：pengdaosong@medlinker.com.
 * <p>
 * Description:
 */

@Route(path = ModuleGroupRouter.RN_FRAGMENT)
public class RnFragment extends BaseReactFragment implements
        DefaultHardwareBackBtnHandler {


    private static final String TAG = "ReactFragment";

    private static final String SCHEME = "sch:";
    private Map<String, String> mParams = new HashMap<>();

    private String mCurModuleName;
    private String mCurPageName;

    private static final String MODULE_NAME = "moduleName";
    private static final String ROUTE_NAME = "routeName";
    private static final String EXTRA = "extra";
    private static final String SPLIT = "/";
    private String mExtra;
    private boolean mIsStopped;
    private boolean mIsVisibleToUser;

    @Retention(RetentionPolicy.SOURCE)
    public @interface RnModuleName {

    }

    @Nullable
    @Override
    protected String getMainComponentName() {
        return "ReactNativeApp";
        // return mCurModuleName;
    }

    private String getCurrentModuleRouterName() {
        return String.format("%s-%s", mCurModuleName, mCurPageName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null == bundle) {
            return;
        }
        String url = bundle.getString(BundleKey.URL);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.trim().startsWith(SPLIT)) {
            url = SCHEME.concat(url);
        }
        Uri uri = Uri.parse(url);
        if (uri != null) {
            try {
                for (String key : uri.getQueryParameterNames()) {
                    this.mParams.put(key, uri.getQueryParameter(key));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != mParams && mParams.size() > 0) {
            mCurModuleName = mParams.get(MODULE_NAME);
            mCurPageName = mParams.get(ROUTE_NAME);
            mExtra = mParams.get(EXTRA);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        FragmentRNCollector.onVisible(getCurrentModuleRouterName(), mIsVisibleToUser && !mIsStopped);
        createWritableMap(isVisibleToUser ? RnEventHelper.EVENT_KEY_FRAGMENT_WILL_APPEAR
                : RnEventHelper.EVENT_KEY_FRAGMENT_WILL_DISAPPEAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsStopped = false;
        FragmentRNCollector.onVisible(getCurrentModuleRouterName(), mIsVisibleToUser && !mIsStopped);
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStopped = true;
        FragmentRNCollector.onVisible(getCurrentModuleRouterName(), mIsVisibleToUser && !mIsStopped);
    }

    private void createWritableMap(String eventName) {
        WritableMap params = new WritableNativeMap();
        params.putString(MODULE_NAME, mCurModuleName);
        params.putString(ROUTE_NAME, mCurPageName);
        RnEventHelper.sendEvent(eventName, params);
    }

    @Override
    protected BaseReactFragmentDelegate createReactFragmentDelegate() {
        ReactFragmentDelegate delegate = new ReactFragmentDelegate(this,
                getMainComponentName());
        Bundle optionBundle = new Bundle();
        optionBundle.putString(MODULE_NAME, mCurModuleName);
        optionBundle.putString(ROUTE_NAME, mCurPageName);
        optionBundle.putString(EXTRA, mExtra);
        delegate.setLaunchOptions(optionBundle);
        return delegate;
    }

    @Override
    public void invokeDefaultOnBackPressed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentRNCollector.remove(getCurrentModuleRouterName());
    }
}
