package com.pds.router.service;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.pds.router.RouterConstants;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:08 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

@Route(path = RouterConstants.SINGLE)
public class SingleService implements IProvider {

    Context mContext;

    public void sayHello(String name) {
        Toast.makeText(mContext, "Hello " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}