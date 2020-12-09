package com.pds.flutter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.router.module.ModuleGroupRouter;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
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
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
//        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "CHANNEL")
//                .setMethodCallHandler(
//                        (call, result) -> {
//                            // Note: this method is invoked on the main thread.
//                            // TODO
//                        }
//                );
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(withNewEngine(FActivity.class).initialRoute("/").build(this));
    }

    public static NewMyEngineIntentBuilder withNewEngine(Class<? extends FlutterActivity> activityClass) {
        return new NewMyEngineIntentBuilder(activityClass);
    }

    //重写创建引擎方法
    public static class NewMyEngineIntentBuilder extends NewEngineIntentBuilder {
        protected NewMyEngineIntentBuilder(Class<? extends FlutterActivity> activityClass) {
            super(activityClass);
        }
    }
}
