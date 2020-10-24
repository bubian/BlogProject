package com.pds.router.core;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.core.interceptor.DegradeServiceImpl;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:03 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ARouterServiceHelper {

    public static <T> T nav(String path){
        return (T) ARouter.getInstance().build(path).navigation();
    }

    // 通过依赖注入解耦:服务管理(二) 发现服务
    @Autowired
    DegradeServiceImpl mDegradeService;

    public ARouterServiceHelper(){
        ARouter.getInstance().inject(this);
    }

    public static ARouterServiceHelper instance(){
        return Lazy.INSTANCE;
    }
    public static class Lazy{
        static final ARouterServiceHelper INSTANCE = new ARouterServiceHelper();
    }
}
