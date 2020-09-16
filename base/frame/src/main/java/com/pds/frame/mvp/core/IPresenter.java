package com.pds.frame.mvp.core;

public interface IPresenter<V extends BaseView> {
    void attachView(V loginView);

    void detachView();
}
