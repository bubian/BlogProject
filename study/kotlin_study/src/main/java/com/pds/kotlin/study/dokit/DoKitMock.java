package com.pds.kotlin.study.dokit;

import android.util.Log;

import com.blog.pds.net.SchedulersCompat;
import com.blog.pds.net.exception.ErrorConsumer;
import com.pds.kotlin.study.net.ApiManager;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/28 11:41 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DoKitMock {
    public static void getLoginSmsCode(String phone, String useType) {
        Log.e("DoKitMock","=====");
        ApiManager.getApi().getLoginSmsCode("text", phone, useType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(entity -> {
                    Log.e("DoKitMock","=====111:" + entity.toString());
                },new ErrorConsumer());
    }
}
