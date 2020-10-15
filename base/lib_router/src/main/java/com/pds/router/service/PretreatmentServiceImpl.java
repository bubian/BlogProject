package com.pds.router.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PretreatmentService;
import com.pds.router.RouterConstants;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:48 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 预处理服务
 */
@Route(path = RouterConstants.SERVICE_PRETREATMENT)
public class PretreatmentServiceImpl implements PretreatmentService {
    @Override
    public boolean onPretreatment(Context context, Postcard postcard) {
        // 跳转前预处理，如果需要自行处理跳转，该方法返回 false 即可
        return false;
    }

    @Override
    public void init(Context context) {

    }
}
