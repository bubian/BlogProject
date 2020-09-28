package com.pds.tools.dokit;

import android.content.Context;

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

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_seekbar_style;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_mode_rb_normal;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public void onClick(Context context) {

    }
}
