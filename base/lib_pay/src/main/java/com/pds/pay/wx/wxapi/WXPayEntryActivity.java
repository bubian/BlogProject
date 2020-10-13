package com.pds.pay.wx.wxapi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pds.pay.R;
import com.pds.pay.wx.WXPayManager;

/**
 * @author pengdaosong
 *
 * 这个界面用于显示第三方app通过微信支付的结果
 *
 * 必须配置在应用包名下，不然支付不回调，所以配置在这里是有问题的
 */
public class WXPayEntryActivity extends AppCompatActivity {

    private WXPayManager mWeiXinHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明处理
        setContentView(R.layout.activity_wx_entry);
        try {
            mWeiXinHelper = WXPayManager.instance(this);
            if (mWeiXinHelper.onHandleIntent(getIntent())) {
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
            if (mWeiXinHelper.onHandleIntent(intent)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mWeiXinHelper.destroy();
        super.onDestroy();
    }
}
