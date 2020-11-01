package com.pds.flutter;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.router.module.ModuleGroupRouter;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/8 2:32 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 参考：https://blog.codemagic.io/flutter-module-android-yaml/
 */
@Route(path = ModuleGroupRouter.FLUTTER_ACTIVITY)
public class FActivity extends FlutterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

    }
}
