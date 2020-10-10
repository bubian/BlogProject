package com.pds.tools.core.dokit;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.pds.tools.core.dokit.httpcarry.ApiKit;
import com.pds.tools.core.dokit.envswitch.EnvSwitchKit;
import java.util.ArrayList;
import java.util.List;

/**
 * 参考：http://xingyun.xiaojukeji.com/docs/dokit#/intro
 *
 * @author: pengdaosong
 * @CreateTime: 2020/9/27 2:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DoKitInit {
    public static final String APP_ID = "34f2baf949b36b874a2e79e3089ff384";
    public static String sProductId;

    public static void init(Application application) {
        DoraemonKit.install(application, getKits());
    }

    /**
     * 使用Dokit平台工具
     *
     * @param productId 在Dokit申请的产品id
     */
    public static void init(Application application, String productId) {
        sProductId = productId;
        DoraemonKit.install(application, getKits(), productId);
    }

    private static List<AbstractKit> getKits() {
        List<AbstractKit> kits = new ArrayList<>();
        kits.add(new EnvSwitchKit(sProductId));
        kits.add(new ApiKit(sProductId));
        return kits;
    }
}
