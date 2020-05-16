package com.pds.frame.mvp.proxy.frag;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;
import com.pds.frame.mvp.proxy.MvpCallBack;

/**
 * 作者: Dream on 2017/8/30 20:48
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public class MvpFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements MvpCallBack<V, P> {

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

    //持有目标对象引用
    //持有目的对象引用
    private FragmentMvpDelegateImpl<V, P> delegate;

    private FragmentMvpDelegateImpl<V, P> getDelegate() {
        if (delegate == null){
            this.delegate = new FragmentMvpDelegateImpl<V, P>(this);
        }
        return delegate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDelegate().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDelegate().onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        getDelegate().onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDelegate().onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getDelegate().onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

}
