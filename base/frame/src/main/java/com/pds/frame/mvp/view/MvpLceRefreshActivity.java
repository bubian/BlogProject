package com.pds.frame.mvp.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.proxy.view.MvpLceActivity;

/**
 * 作者: Dream on 2017/8/30 22:25
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//集成下拉刷新组件(自己定义->采用任意下拉刷新组件)
//默认情况下，我写了一个(演示如何实现)
public abstract class MvpLceRefreshActivity<D, V extends BaseView, P extends BasePresenter<V>> extends MvpLceActivity<D, V, P> {

    private boolean isDownRefresh;
    private boolean isPullToRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRefreshView(getWindow().getDecorView());
    }

    /**
     * 初始化下拉刷新组件
     *
     * @param contentView
     */
    public void initRefreshView(View contentView) {

    }

    public void refreshData(boolean isDownRefresh){
        this.isDownRefresh = isDownRefresh;
        this.isPullToRefresh = true;
    }

}
