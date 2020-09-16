package com.pds.frame.mvp.core;

import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/10/18.
 */

public class MvpFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements MvpCallBack<V, P> {

    private P mPresenter;
    private V mV;
    private MvpProxy<V, P> mMvpImpl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMvpImpl = new MvpProxy<>(this);
    }


    @Override
    public P createPresenter() {
        Type superClassType = MvpActivity.class.getGenericSuperclass();
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
    public V createView() {
        Type superClassType = MvpFragment.class.getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            try {
                Class superClass = (Class) superClassType;
                Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
                Class<V> presenterClassType = (Class<V>) types[0];
                if (superClass.isAssignableFrom(presenterClassType)) {
                    mV = (V) MvpFragment.this;
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mMvpImpl != null) {
            mMvpImpl.onDestroy();
        }
    }
}
