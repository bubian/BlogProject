package com.pds.frame.mvp.proxy;


import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/10/18.
 */

public interface MvpCallBack <V extends BaseView,P extends BasePresenter<V>>{
    //创建P层
    P createPresenter();
    //创建V层
    V createView();
    //得到P层
    P getP();

    //设置P层
    void setP(P presenter);

    //得到V层
    V getV();

    //设置V层
    void setV(V view);
}
