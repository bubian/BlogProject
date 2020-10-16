package com.pds.router;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.pds.router.core.ARouterHelper;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 7:47 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
// 我们经常需要在目标页面中配置一些属性，比方说"是否需要登陆"之类的
// 可以通过 Route 注解中的 extras 属性进行扩展，这个属性是一个 int值，换句话说，单个int有4字节，也就是32位，可以配置32个开关
// 剩下的可以自行发挥，通过字节操作可以标识32个开关，通过开关标记目标页面的一些属性，在拦截器中可以拿到这个标记进行业务逻辑判断
@Route(path = ARouterConstants.PARAMS_ACTIVITY, extras = 1)
public class ParamsActivity extends Activity {

    @Autowired
    String key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String value = getIntent().getStringExtra("key1");
        ARouterHelper.nav(this, ARouterConstants.SERVICE_ACTIVITY, new NavCallback() {
            @Override
            public void onFound(Postcard postcard) {
                Log.d("ARouter", "找到了");
            }

            @Override
            public void onLost(Postcard postcard) {
                Log.d("ARouter", "找不到了");
            }

            @Override
            public void onArrival(Postcard postcard) {
                Log.d("ARouter", "跳转完了");
            }

            @Override
            public void onInterrupt(Postcard postcard) {
                Log.d("ARouter", "被拦截了");
            }
        });
    }
}
