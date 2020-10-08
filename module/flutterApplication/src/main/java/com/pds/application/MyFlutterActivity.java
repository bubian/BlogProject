package com.pds.application;

import android.os.Bundle;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/8 2:32 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 参考：https://blog.codemagic.io/flutter-module-android-yaml/
 */
public class MyFlutterActivity extends FlutterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

    }
}
