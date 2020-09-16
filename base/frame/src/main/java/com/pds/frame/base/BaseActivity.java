package com.pds.frame.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: pengDaoSong
 * CreateTime:  2020-09-16 15:21
 * Description: 自定义顶层Activity基类，尽量不要包含特定业务处理
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
