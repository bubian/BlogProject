package com.pds.frame.mvp.proxy.view;

import android.view.View;
import com.pds.frame.mvp.proxy.anim.DefaultLceAnimator;
import com.pds.frame.mvp.proxy.anim.ILceAnimator;

/**
 * 作者: Dream on 2017/8/30 21:20
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public class MvpLceViewImpl<D> implements MvpLceView<D> {

    //持有目标策略
    private ILceAnimator lceAnimator;

    public void setLceAnimator(ILceAnimator lceAnimator) {
        this.lceAnimator = lceAnimator;
    }

    public ILceAnimator getLceAnimator() {
        if (this.lceAnimator == null){
            this.lceAnimator = new DefaultLceAnimator();
        }
        return lceAnimator;
    }

    private View loadingView;
    private View contentView;
    private View errorView;

    public void initLceView(View rootView){
        if (rootView == null){
            throw new NullPointerException("rootView不能够为空");
        }
        if (this.loadingView == null){
//            this.loadingView = rootView.findViewById(R.id.loadingView);
        }
        if (this.contentView == null){
//            this.contentView = rootView.findViewById(R.id.contentView);
        }
        if (this.errorView == null){
//            this.errorView = rootView.findViewById(R.id.errorView);
        }
        if (this.loadingView == null){
            throw new NullPointerException("loadingView不能够为空");
        }
        if (this.contentView == null){
            throw new NullPointerException("contentView不能够为空");
        }
        if (this.errorView == null){
            throw new NullPointerException("errorView不能够为空");
        }
    }

    public void setOnErrorViewClickListener(View.OnClickListener onClickListener){
        if (this.errorView != null){
            this.errorView.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void showLoading(boolean isPullToRefresh) {
        if (!isPullToRefresh){
            getLceAnimator().showLoadingView(this.loadingView, this.contentView, this.errorView);
        }
    }

    @Override
    public void showContent(boolean isPullToRefresh) {
        if (!isPullToRefresh){
            getLceAnimator().showLoadingView(this.loadingView, this.contentView, this.errorView);
        }
    }

    @Override
    public void showError(boolean isPullToRefresh) {
        if (!isPullToRefresh){
            getLceAnimator().showLoadingView(this.loadingView, this.contentView, this.errorView);
        }
    }

    @Override
    public void bindData(D data, boolean isPullToRefresh) {

    }

    @Override
    public void loadData(boolean isPullToRefresh) {

    }

    @Override
    public void onResult(String result) {

    }
}
