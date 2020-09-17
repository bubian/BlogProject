package com.pds.blog.frame.mvp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pds.frame.log.Lg;
import com.pds.frame.mvp.core.MvpActivity;

public class MvpSampleActivity extends MvpActivity<ICTViewImpl, CTPresenter> {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
        getP().login();
    }

    private View buildView() {
        mTextView = new TextView(this);
        return mTextView;
    }


//    @Override
//    public void onResult(String result) {
//        Lg.i("frame-mvp-sample:result = " + result);
//    }
}
