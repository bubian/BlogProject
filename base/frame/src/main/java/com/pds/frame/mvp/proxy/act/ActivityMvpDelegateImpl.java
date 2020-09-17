package com.pds.frame.mvp.proxy.act;

import android.os.Bundle;

import com.pds.frame.mvp.core.BaseP;
import com.pds.frame.mvp.core.BaseV;
import com.pds.frame.mvp.core.MvpCallBack;
import com.pds.frame.mvp.core.MvpProxy;

//目标对象：具体的实现->生命周期实现
public class ActivityMvpDelegateImpl<V extends BaseV, P extends BaseP<V>> implements ActivityMvpDelegate<V, P> {

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
        getCallbackProxy().createP();
        getCallbackProxy().createV();
        getCallbackProxy().attachV();
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
        getCallbackProxy().detachV();
    }

}
