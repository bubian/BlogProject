package com.pds.frame.mvp.core;

public abstract class BaseP<V extends BaseV> implements PInterface<V> {

    private V mV;

    public V getV() {
        return mV;
    }

    public void attachV(V mV){
        this.mV = mV;
    }

    public void detachV(){
        this.mV = null;
    }
}
