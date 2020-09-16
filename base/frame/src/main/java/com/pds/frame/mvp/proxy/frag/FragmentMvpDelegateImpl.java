package com.pds.frame.mvp.proxy.frag;

import android.os.Bundle;
import android.view.View;

import com.pds.frame.mvp.core.BasePresenter;
import com.pds.frame.mvp.core.BaseView;
import com.pds.frame.mvp.core.MvpCallBack;
import com.pds.frame.mvp.core.MvpProxy;

//目标对象
public class FragmentMvpDelegateImpl<V extends BaseView, P extends BasePresenter<V>> implements FragmentMvpDelegate<V, P> {

    //实现功能->绑定UI层和接触绑定
    private MvpCallBack<V, P> callback;
    private MvpProxy<V, P> callbackProxy;

    public FragmentMvpDelegateImpl(MvpCallBack<V, P> callback){
        this.callback = callback;
    }

    private MvpProxy<V, P> getCallbackProxy(){
        //代理对象
        if (callback != null) {
            this.callbackProxy = new MvpProxy<V, P>(callback);
        }
        return this.callbackProxy;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //绑定实现
        //回调
        getCallbackProxy().createPresenter();
        getCallbackProxy().createView();
        getCallbackProxy().attachView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onDestroy() {
        getCallbackProxy().detachView();
    }
}
