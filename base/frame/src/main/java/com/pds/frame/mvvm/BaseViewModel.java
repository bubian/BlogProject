package com.pds.frame.mvvm;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 13:37
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BaseViewModel extends ViewModel implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume(){

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause(){}

    protected void load(){}
}
