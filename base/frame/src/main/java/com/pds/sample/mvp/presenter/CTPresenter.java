package com.pds.sample.mvp.presenter;

import com.pds.frame.mvp.core.BasePresenter;
import com.pds.sample.mvp.mode.CTMode;
import com.pds.sample.mvp.ICTView;

public class CTPresenter extends BasePresenter<ICTView> {
    CTMode mM;

    public CTPresenter(){
        mM = new CTMode();
    }

    public void login(String ne,String pw,String cc){ }
}
