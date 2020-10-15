package com.pds.sample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.base.act.BaseActivity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/21 9:56 AM
 * @Email: pengdaosong@medlinker.com
 * @Description: 供外部类调用
 */
public class StartAppActivity extends BaseActivity {

    private static final String NOTICE_JUMP_URL = "noticeJumpUrl";

    /**
     * TODO 等具体的用户详情接口编写完成后补充
     */
    private boolean mRegisteredUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置Activity背景透明
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        jump();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        jump();
    }

    private void jump() {
        // try catch：拒绝服务攻击漏洞
        try {
            if (getIntent().getData() != null && mRegisteredUser && !getIntent().getData()
                    .toString().isEmpty()) {
                String jumpUrl = getIntent().getData().toString();
                ARouter.getInstance().build(jumpUrl).navigation(this);
                finish();
                return;
            }

            String jumpUrl = getIntent().getStringExtra(NOTICE_JUMP_URL);
            if (TextUtils.isEmpty(jumpUrl) && getIntent().getData() != null) {
                jumpUrl = getIntent().getData().toString();
            }
            // 参数BundleConstant.KEY：是否是从外界跳转进入
            ARouter.getInstance().build(jumpUrl)
                    .navigation(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
