package com.pds.frame.mvp.proxy.frag;

import android.os.Bundle;
import android.view.View;

import com.pds.frame.mvp.core.BaseP;
import com.pds.frame.mvp.core.BaseV;

public interface FragmentMvpDelegate<V extends BaseV, P extends BaseP<V>> {

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
