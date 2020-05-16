package com.pds.frame.mvp.presenter;

import com.pds.frame.mvp.view.BaseView;

/**
 * 作者: Dream on 2017/8/29 22:24
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public interface IPresenter<V extends BaseView> {
    void attachView(V loginView);
    void detachView();
}
