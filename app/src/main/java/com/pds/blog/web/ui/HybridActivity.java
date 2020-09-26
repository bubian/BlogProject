package com.pds.blog.web.ui;

import android.graphics.PixelFormat;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 10:59
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HybridActivity extends HybridBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //X5兼容网页视频闪烁情况,这个对宿主没什么影响，建议声明
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new HybridFragment(), HybridFragment.class.getSimpleName()).commit();
    }
}
