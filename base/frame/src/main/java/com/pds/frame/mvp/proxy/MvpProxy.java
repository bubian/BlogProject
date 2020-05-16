package com.pds.frame.mvp.proxy;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/10/18.
 */

public class MvpProxy<V extends BaseView,P extends BasePresenter<V>> implements MvpCallBack<V,P>{

    private P mP;
    private V mV;
    private MvpCallBack<V,P> mvpCallBack;   ///写成MvpCallBack会报错
    public P getPresenter() {
        return mP;
    }

    public MvpProxy(MvpCallBack<V,P> callBack){
        mvpCallBack = callBack;
        init();
    }

    private void init() {
        mP = mvpCallBack.createPresenter();
        if (null == mP){
            throw new NullPointerException("p of mvp is null");
        }
        mV = mvpCallBack.createView();
        if (null == mV){
            throw new NullPointerException("v of mvp is null");
        }
        mP.attachView(mV);
    }
    public void onDestroy() {
        if (mP != null){
            mP.detachView();
        }
    }

    @Override
    public P createPresenter() {
        P mP = this.mvpCallBack.getP();
        if (mP == null){
            mP = this.mvpCallBack.createPresenter();
        }
        if (mP == null) {
            throw new NullPointerException("mP，空指针异常...");
        }
        this.mvpCallBack.setP(mP);
        return mP;
    }

    @Override
    public V createView() {
        V mV = this.mvpCallBack.getV();
        if (mV == null){
            mV = this.mvpCallBack.createView();
        }
        if (mV == null) {
            throw new NullPointerException("mV，空指针异常...");
        }
        this.mvpCallBack.setV(mV);
        return mV;
    }

    @Override
    public P getP() {
        P mP = this.mvpCallBack.getP();
        if (mP == null) {
            throw new NullPointerException("mP，空指针异常...");
        }
        return mP;
    }

    @Override
    public void setP(P presenter) {
        this.mvpCallBack.setP(presenter);
    }

    @Override
    public V getV() {
        V mV = this.mvpCallBack.getV();
        if (mP == null) {
            throw new NullPointerException("mP，空指针异常...");
        }
        return mV;
    }

    @Override
    public void setV(V v) {
        this.mvpCallBack.setV(v);
    }
    public void attachView(){
        getPresenter().attachView(getV());
    }

    public void detachView(){
        getPresenter().detachView();
    }
}
