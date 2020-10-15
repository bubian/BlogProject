package com.pds.router.interceptor;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.pds.router.RouterConstants;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:38 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 自定义全局降级策略
 */
@Route(path = RouterConstants.SERVICE_DEGRADE)
public class DegradeServiceImpl implements DegradeService {
    @Override
    public void onLost(Context context, Postcard postcard) {

    }

    @Override
    public void init(Context context) {

    }
}
