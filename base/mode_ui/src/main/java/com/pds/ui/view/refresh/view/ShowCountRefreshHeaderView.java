package com.pds.ui.view.refresh.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pds.ui.R;
import com.pds.ui.view.refresh.cb.IRefreshTrigger;
import com.pds.util.unit.UnitConversionUtils;

/**
 * 下拉刷新，刷新后显示刷新数据条数
 *
 * @author hmy
 */
public class ShowCountRefreshHeaderView extends LinearLayout implements IRefreshTrigger {

    private CircleLoadingView mLoadingView;
    private TextView mLoadingTv;
    private TextView mLoadingCompleteTv;

    private int mLoadedDataCount = 0;
    private String mRefreshCompleteText;

    public ShowCountRefreshHeaderView(Context context) {
        this(context, null);
    }

    public ShowCountRefreshHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UnitConversionUtils.dip2px(getContext(), 50)));
        LayoutInflater.from(getContext()).inflate(R.layout.layout_show_count_refresh_header, this, true);

        mLoadingView = findViewById(R.id.v_refresh_loading);
        mLoadingTv = findViewById(R.id.tv_refresh_loading_text);
        mLoadingCompleteTv = findViewById(R.id.tv_refresh_load_count);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onPullDownState(float progress) {
        setLoadingShowState(true);
        mLoadingView.changeRotation(progress);
        mLoadingTv.setText(getResources().getString(R.string.text_pull_down_refresh));
    }

    @Override
    public void onRefreshing() {
        setLoadingShowState(true);
        mLoadingView.startRotateAnimation();
        mLoadingTv.setText(getResources().getString(R.string.text_refreshing));
    }

    @Override
    public void onReleaseToRefresh() {
        mLoadingTv.setText(getResources().getString(R.string.text_freed_refresh));
    }

    @Override
    public void onComplete() {
        mLoadingView.cancelAnimator();
        setLoadingShowState(false);
        String text;
        if (TextUtils.isEmpty(mRefreshCompleteText)) {
            text = mLoadedDataCount >= 0
                    ? String.format(getResources().getString(R.string.text_refresh_count), mLoadedDataCount)
                    : getResources().getString(R.string.text_refresh_fail);
        } else {
            text = mRefreshCompleteText;
        }
        mLoadingCompleteTv.setText(text);
    }

    @Override
    public void init() {
        setLoadingShowState(true);
        mLoadingView.cancelAnimator();
        mLoadingView.changeRotation(0);
        mLoadingTv.setText(getResources().getString(R.string.text_pull_down_refresh));
    }

    private void setLoadingShowState(boolean isLoading) {
        mLoadingCompleteTv.setVisibility(isLoading ? GONE : VISIBLE);
        mLoadingView.setVisibility(isLoading ? VISIBLE : GONE);
        mLoadingTv.setVisibility(isLoading ? VISIBLE : GONE);
    }

    /**
     * 刷新获取的数据数量
     */
    public void setRefreshCompleteCount(int count) {
        mLoadedDataCount = count;
        mRefreshCompleteText = "";
    }

    /**
     * 刷新失败
     */
    public void setRefreshFail() {
        mLoadedDataCount = -1;
        mRefreshCompleteText = "";
    }

    /**
     * 设置刷新完成后的文字
     */
    public void setRefreshCompleteText(String text) {
        mRefreshCompleteText = text;
    }
}
