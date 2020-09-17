package com.pds.frame.mvp.core;


/**
 * Created by Administrator on 2017/10/18.
 */

public interface MvpCallBack <V extends BaseV,P extends BaseP<V>>{
    //创建P层
    P createP();
    //创建V层
    V createV();
    //得到P层
    P getP();

    //设置P层
    void setP(P presenter);

    //得到V层
    V getV();

    //设置V层
    void setV(V view);
}
