package com.pds.blog.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pds.blog.R;
import com.pds.pay.wxmin.WXMinPayManager;


/**
 * @author pengdaosong
 */
public class WXEntryActivity extends Activity {

    private WXMinPayManager mWXMinPayManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        try {
            mWXMinPayManager = WXMinPayManager.instance(this);
            if (mWXMinPayManager.onHandleIntent(getIntent())) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (mWXMinPayManager.onHandleIntent(intent)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mWXMinPayManager) {
            mWXMinPayManager.destroy();
        }
        super.onDestroy();
    }
}