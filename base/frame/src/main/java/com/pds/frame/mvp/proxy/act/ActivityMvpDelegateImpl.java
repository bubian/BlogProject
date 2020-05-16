package com.pds.frame.mvp.proxy.act;

import android.os.Bundle;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;
import com.pds.frame.mvp.proxy.MvpCallBack;
import com.pds.frame.mvp.proxy.MvpProxy;

/**
 * 作者: Dream on 2017/8/29 22:55
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//目标对象：具体的实现->生命周期实现
public class ActivityMvpDelegateImpl<V extends BaseView, P extends BasePresenter<V>> implements ActivityMvpDelegate<V, P> {

    private MvpCallBack<V, P> callback;
    private MvpProxy<V, P> callbackProxy;

    public ActivityMvpDelegateImpl(MvpCallBack<V, P> callback){
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
        //绑定实现
        //回调
        getCallbackProxy().createPresenter();
        getCallbackProxy().createView();
        getCallbackProxy().attachView();
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
    public void onRestart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        getCallbackProxy().detachView();
    }

}
