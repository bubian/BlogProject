package com.pds.blog.frame.mvp;

import com.pds.frame.mvp.core.BaseP;

public class CTPresenter extends BaseP<ICTViewImpl> {
    CTMode mM;

    public CTPresenter() {
        mM = new CTMode();
    }

    public void login() {
        mM.login();
        getV().onResult("成功");
    }
}
