package com.pds.tools.dokit;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.pds.tools.R;
import com.pds.tools.module.med.MedEnvInterceptor;
import com.pds.tools.module.med.MedNetEnvSwitchActivity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/28 10:13 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class MedEnvSwitchKit extends AbstractKit {

    private MedEnvInterceptor mEnvInterceptor;

    public MedEnvSwitchKit(String kitId) {
        mEnvInterceptor = new MedEnvInterceptor();
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
        Intent intent = new Intent(context, MedNetEnvSwitchActivity.class);
        context.startActivity(intent);
    }

}
