package com.pds.frame.mvp.proxy.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.frame.mvp.presenter.BasePresenter;
import com.pds.frame.mvp.view.BaseView;
import com.pds.frame.mvp.proxy.act.MvpActivity;
import com.pds.frame.mvp.proxy.anim.ILceAnimator;

/**
 * 作者: Dream on 2017/8/30 21:14
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public abstract class MvpLceActivity<D, V extends BaseView, P extends BasePresenter<V>> extends MvpActivity<V, P> implements MvpLceView<D> {

    //持有目标对象引用
    private MvpLceViewImpl<D> mvpLceView;

    private MvpLceViewImpl<D> getMvpLceView(){
        if (mvpLceView == null){
            mvpLceView = new MvpLceViewImpl<D>();
        }
        return mvpLceView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定
        getMvpLceView().initLceView(getWindow().getDecorView());
        getMvpLceView().setOnErrorViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickError(view);
            }
        });
    }

    public void onClickError(View view){
        //默认重写加载数据
        loadData(false);
    }

    //子类重写该方法，重写配置策略
    public void setLceAnimator(ILceAnimator lceAnimator){
        getMvpLceView().setLceAnimator(lceAnimator);
    }

    @Override
    public void showLoading(boolean isPullToRefresh) {
        getMvpLceView().showLoading(isPullToRefresh);
    }

    @Override
    public void showContent(boolean isPullToRefresh) {
        getMvpLceView().showLoading(isPullToRefresh);
    }

    @Override
    public void showError(boolean isPullToRefresh) {
        getMvpLceView().showError(isPullToRefresh);
    }

    @Override
    public void bindData(D data, boolean isPullToRefresh) {
        getMvpLceView().bindData(data, isPullToRefresh);
    }

    @Override
    public void loadData(boolean isPullToRefresh) {
        getMvpLceView().loadData(isPullToRefresh);
    }

}
