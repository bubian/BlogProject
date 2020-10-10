package com.pds.tools.core.dokit.envswitch;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.pds.tools.R;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/28 10:13 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class EnvSwitchKit extends AbstractKit {

    private EnvInterceptor mEnvInterceptor;

    public EnvSwitchKit(String kitId) {
        mEnvInterceptor = new EnvInterceptor();
    }

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getIcon() {
        return R.mipmap.med;
    }

    @Override
    public int getName() {
        return R.string.med_env_switch;
    }

    @Override
    public String innerKitId() {
        return "med_env_switch";
    }

    @Override
    public void onAppInit(Context context) {
        OkHttpHook.globalInterceptors.add(0, mEnvInterceptor);
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, NetEnvSwitchActivity.class);
        context.startActivity(intent);
    }

}
