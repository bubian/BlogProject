package com.pds.sample.mvp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pds.frame.mvp.core.MvpActivity;
import com.pds.sample.mvp.presenter.CTPresenter;

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
}
