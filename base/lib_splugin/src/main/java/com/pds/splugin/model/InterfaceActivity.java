package com.pds.splugin.model;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public interface InterfaceActivity {
    void attach(Activity proxyActivity);

    /**
     * 生命周期
     * @param savedInstanceState
     */
    void onCreate(Bundle savedInstanceState);
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
    void onSaveInstanceState(Bundle outState);
    boolean onTouchEvent(MotionEvent event);
    void onBackPressed();
}
