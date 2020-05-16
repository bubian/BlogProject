package com.pds.frame.mvp.proxy.frag;

import android.os.Bundle;
import android.view.View;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;

/**
 * 作者: Dream on 2017/8/30 20:52
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public interface FragmentMvpDelegate<V extends BaseView, P extends BasePresenter<V>> {

    void onCreate(Bundle savedInstanceState);

    void onActivityCreated(Bundle savedInstanceState);

    void onViewCreated(View view, Bundle savedInstanceState);

    void onStart();

    void onPause();

    void onResume();

    void onStop();

    void onDestroyView();

    void onDestroy();

}
