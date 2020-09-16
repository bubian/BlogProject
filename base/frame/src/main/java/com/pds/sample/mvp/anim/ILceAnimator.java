package com.pds.sample.mvp.anim;

import android.view.View;

/**
 * 作者: Dream on 2017/8/30 21:29
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

//策略接口
public interface ILceAnimator {

    /**
     * 显示加载页面->动画加载
     *
     * @param loadingView
     * @param contentView
     * @param errorView
     */
    void showLoadingView(View loadingView, View contentView, View errorView);

    /**
     * 显示内容页面
     *
     * @param loadingView
     * @param contentView
     * @param errorView
     */
    void showContentView(View loadingView, View contentView, View errorView);

    /**
     * 显示错误页面
     *
     * @param loadingView
     * @param contentView
     * @param errorView
     */
    void showErrorView(View loadingView, View contentView, View errorView);

}
