package com.pds.frame.mvp.proxy.view;

import com.pds.frame.mvp.view.BaseView;

/**
 * 作者: Dream on 2017/8/30 21:07
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public interface MvpLceView<D> extends BaseView {

    /**
     * 是否显示加载页面(是否是下拉刷新组件)
     *
     * @param isPullToRefresh 如果你是下拉刷新组件，那么自带刷新，不需要加载页面，否则需要加载页面
     */
    void showLoading(boolean isPullToRefresh);

    /**
     * 显示内容页面
     * @param isPullToRefresh
     */
    void showContent(boolean isPullToRefresh);

    /**
     * 显示错误页面
     * @param isPullToRefresh
     */
    void showError(boolean isPullToRefresh);

    /**
     * 绑定数据
     * @param data
     */
    void bindData(D data, boolean isPullToRefresh);

    /**
     * 加载数据
     * @param isPullToRefresh
     */
    void loadData(boolean isPullToRefresh);

}
