package com.pds.sample.mvp;

import android.os.Bundle;
import android.view.View;

import com.pds.frame.mvp.core.BaseView;
import com.pds.frame.mvp.core.BasePresenter;

/**
 * 作者: Dream on 2017/8/30 22:25
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//集成下拉刷新组件(自己定义->采用任意下拉刷新组件)
//默认情况下，我写了一个(演示如何实现)
public abstract class MvpLceRefreshFragment<D, V extends BaseView, P extends BasePresenter<V>> extends
        MvpLceFragment<D, V, P> {

    private boolean isDownRefresh;
    private boolean isPullToRefresh;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshView(view);
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
