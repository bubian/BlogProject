package com.pds.router.core.interceptor;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.pds.router.core.RouterConstants;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:38 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 自定义全局降级策略
 * 报：ARouter::Compiler An exception is encountered, [null]错误，添加androidx.appcompat:appcompat:1.1.0解决问题
 */
@Route(path = RouterConstants.SERVICE_DEGRADE)
public class DegradeServiceImpl implements DegradeService {
    @Override
    public void onLost(Context context, Postcard postcard) {
        Toast.makeText(context,"ARouter jump fail",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void init(Context context) {

    }
}
