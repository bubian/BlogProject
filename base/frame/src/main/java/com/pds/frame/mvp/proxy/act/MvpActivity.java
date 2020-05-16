package com.pds.frame.mvp.proxy.act;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;
import com.pds.frame.mvp.proxy.MvpCallBack;


/**
 * 作者: Dream on 2017/8/29 22:40
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//
public abstract class MvpActivity<V extends BaseView, P extends BasePresenter<V>> extends Activity implements MvpCallBack<V, P> {

    //持有目的对象引用
    private ActivityMvpDelegateImpl<V, P> delegate;

    private ActivityMvpDelegateImpl<V, P> getDelegate() {
        if (delegate == null){
            this.delegate = new ActivityMvpDelegateImpl<V, P>(this);
        }
        return delegate;
    }

    private P presenter;
    private V view;

    @Override
    public P createPresenter() {
        return this.presenter;
    }

    @Override
    public V createView() {
        return this.view;
    }

    @Override
    public P getP() {
        return this.presenter;
    }

    @Override
    public void setP(P presenter) {
        this.presenter = presenter;
    }

    @Override
    public V getV() {
        return this.view;
    }

    @Override
    public void setV(V view) {
        this.view = view;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDelegate().onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDelegate().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getDelegate().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
}
