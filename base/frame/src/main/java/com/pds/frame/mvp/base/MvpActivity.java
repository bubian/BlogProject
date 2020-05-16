package com.pds.frame.mvp.base;

import android.app.Activity;
import android.os.Bundle;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.proxy.MvpCallBack;
import com.pds.frame.mvp.proxy.MvpProxy;
import com.pds.frame.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/10/18.
 */

public abstract class MvpActivity<V extends BaseView,P extends BasePresenter<V>> extends Activity implements MvpCallBack<V, P> {

    private static final String TAG = "BaseActivity";
    private P presenter;
    private MvpProxy<V, P> mMvpImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == mMvpImpl){
            mMvpImpl = new MvpProxy<>(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMvpImpl){
            mMvpImpl.onDestroy();
        }
    }
}
