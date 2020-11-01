package com.pds.flutter;

import android.app.Activity;
import android.content.Intent;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/8 2:04 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class FlutterHelper {
    public static void startFlutter(Activity activity) {
        Intent intent = new Intent(activity, FActivity.class);
        activity.startActivity(intent);
    }
}
