package com.pds.hilt;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/16 2:02 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@AndroidEntryPoint
public class ExampleActivity extends AppCompatActivity {

    @Inject
    AnalyticsAdapter analytics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("======>",analytics.toString());
    }
}