package com.pds.frame.mvp.core;

import android.os.Bundle;

import com.pds.frame.base.BaseActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/10/18.
 * Description: 自定义mvp层Activity，如果需要使用mvp，请继承改类
 */

public abstract class MvpActivity<V extends BaseV, P extends BaseP<V>> extends BaseActivity implements MvpCallBack<V, P> {

    private P mPresenter;
    private V mV;
    private MvpProxy<V, P> mMvpImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == mMvpImpl) {
            mMvpImpl = new MvpProxy<>(this);
        }
    }

    @Override
    public P createP() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<P> presenterClassType = (Class<P>) types[1];
                mPresenter = presenterClassType.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mPresenter;
    }

    @Override
    public V createV() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            try {
                Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
                Class<V> presenterClassType = (Class<V>) types[0];
                if (MvpActivity.this instanceof BaseV) {
                    mV = (V) MvpActivity.this;
                } else {
                    mV = presenterClassType.newInstance();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mV;
    }

    @Override
    public P getP() {
        return mPresenter;
    }

    @Override
    public void setP(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public V getV() {
        return mV;
    }

    @Override
    public void setV(V v) {
        mV = v;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMvpImpl) {
            mMvpImpl.onDestroy();
        }
    }
}
