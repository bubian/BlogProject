package com.pds.frame.mvp.core;

public abstract class BasePresenter<V extends BaseView> implements IPresenter<V> {

    private V mV;

    public V getView() {
        return mV;
    }

    public void attachView(V mV){
        this.mV = mV;
    }

    public void detachView(){
        this.mV = null;
    }
}
