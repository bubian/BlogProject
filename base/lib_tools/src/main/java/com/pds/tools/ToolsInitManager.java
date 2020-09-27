package com.pds.tools;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/27 2:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ToolsInitManager {

    public static void init(Application application){
        DoraemonKit.install(application);
    }
}
