package com.pds.tools.core.dokit.httpcarry;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.pds.tools.R;
import com.pds.tools.core.dokit.httpcarry.act.ApiActivity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 2:54 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiKit extends AbstractKit {

    private ApiInterceptor mInterceptor;

    public ApiKit(String kitId) {
        mInterceptor = new ApiInterceptor();
    }

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getIcon() {
        return R.mipmap.api;
    }

    @Override
    public int getName() {
        return R.string.app_name;
    }

    @Override
    public String innerKitId() {
        return "med_api";
    }

    @Override
    public void onAppInit(Context context) {
        OkHttpHook.globalInterceptors.add(mInterceptor);
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, ApiActivity.class);
        context.startActivity(intent);
    }
}
