package com.pds.frame.mvp.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pds.frame.mvp.base.MvpActivity;
import com.pds.frame.mvp.presenter.CTPresenter;

public class MainActivity extends MvpActivity<ICTView, CTPresenter> implements ICTView {

    private static final String TAG = "MainActivity";
    private CTPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CTPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onResult(String result) {
        Log.i(TAG,"result = "+result);
    }

    public void login(View view){
        if (null == presenter)return;
        presenter.login("13982000002","jjr123","1688");
    }

    @Override
    public CTPresenter createPresenter() {
        return new CTPresenter();
    }

    @Override
    public ICTView createView() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    @Override
    public CTPresenter getP() {
        return null;
    }

    @Override
    public void setP(CTPresenter presenter) {

    }

    @Override
    public ICTView getV() {
        return null;
    }

    @Override
    public void setV(ICTView view) {

    }
}
