package com.pds.blog.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import io.reactivex.Observable;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:13
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BaseActivity extends RxAppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"life cycle：onCreate");

        Observable.create(emitter -> {}).compose(bindToLifecycle()).subscribe();
    }
}