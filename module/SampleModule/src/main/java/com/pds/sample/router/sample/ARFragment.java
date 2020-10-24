package com.pds.sample.router.sample;

import android.app.Fragment;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:23 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

@Route(path = ARouterConstants.AR_FRAGMENT)
public class ARFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
