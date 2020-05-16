package com.pds.frame.mvp.proxy.act;

import android.os.Bundle;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;

/**
 * 作者: Dream on 2017/8/29 22:51
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//目标接口：针对的是Activity目标接口->生命周期进行代理
public interface ActivityMvpDelegate<V extends BaseView, P extends BasePresenter<V>> {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onPause();

    void onResume();

    void onRestart();

    void onStop();

    void onDestroy();

}
