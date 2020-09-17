package com.pds.frame.mvp.proxy.act;

import android.os.Bundle;

import com.pds.frame.mvp.core.BaseP;
import com.pds.frame.mvp.core.BaseV;

//目标接口：针对的是Activity目标接口->生命周期进行代理
public interface ActivityMvpDelegate<V extends BaseV, P extends BaseP<V>> {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onPause();

    void onResume();

    void onRestart();

    void onStop();

    void onDestroy();

}
