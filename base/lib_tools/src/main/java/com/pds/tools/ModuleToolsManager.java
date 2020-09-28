package com.pds.tools;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.pds.tools.dokit.EnvSwitchKit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/27 2:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleToolsManager {

    public static void init(Application application){
        DoraemonKit.install(application);
    }

    /**
     * 使用Dokit平台工具
     * @param application
     * @param productId 在Dokit申请的产品id
     */
    public static void init(Application application,String productId){
        DoraemonKit.install(application, getKits(),productId);
    }

    private static List<AbstractKit> getKits(){
        List<AbstractKit> kits = new ArrayList<>();
        kits.add(new EnvSwitchKit());
        return kits;
    }
}
