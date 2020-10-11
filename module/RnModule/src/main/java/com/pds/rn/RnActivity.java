package com.pds.rn;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/11 12:27 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class RnActivity extends ReactActivity {
    private static final String ROOT_ROUTE = "ReactNativeApp";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getMainComponentName() {
        return ROOT_ROUTE;
    }
}
