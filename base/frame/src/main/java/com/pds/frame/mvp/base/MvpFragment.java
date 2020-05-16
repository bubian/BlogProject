package com.pds.frame.mvp.base;

import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/10/18.
 */

public abstract class MvpFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment{

    private P mP;
    private V mV;

    public P getPresenter() {
        return mP;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mP = createPresenter();
        if (mP == null) {
            throw new NullPointerException("presenter，空指针异常...");
        }
        mV = createView();
        if (mV == null){
            throw new NullPointerException("view，空指针异常...");
        }
        mP.attachView(mV);
    }

    protected abstract P createPresenter();
    protected abstract V createView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mP != null){
            mP.detachView();
        }
    }
}
