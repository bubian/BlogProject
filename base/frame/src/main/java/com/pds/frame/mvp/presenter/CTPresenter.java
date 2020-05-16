package com.pds.frame.mvp.presenter;

import com.pds.frame.mvp.mode.CTMode;
import com.pds.frame.mvp.view.ICTView;

public class CTPresenter extends BasePresenter<ICTView> {
    CTMode mM;

    public CTPresenter(){
        mM = new CTMode();
    }

    public void login(String ne,String pw,String cc){ }
}
