package com.pds.ui.view.refresh.header;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:25
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pds.ui.R;
import com.pds.ui.view.refresh.cb.IRefreshTrigger;

import java.util.Random;

public class ZoomHeaderView extends FrameLayout implements IRefreshTrigger {

    private ImageView loadingView;
    private TextView refreshState;

    static final String[] sTIPS = {
            "看前沿医疗资讯，上医联头条", "喜欢我们记得分享哟", "越用越懂你", "别忘了，右上方还有知识库等你发现"
    };

    private int tipsIndex;
    private Random mRandom;
    private IRefreshTrigger mRefreshTrigger;

    public ZoomHeaderView(Context context) {
        this(context, null);
    }

    public ZoomHeaderView callback(IRefreshTrigger refreshTrigger){
        this.mRefreshTrigger = refreshTrigger;
        return this;
    }

    public ZoomHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
        mRandom = new Random();
    }
    private void setupViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.header_zoom, this);
        view.setMinimumHeight(0);
        view.setVisibility(GONE);
        loadingView = view.findViewById(R.id.iv_loadingview);
        refreshState = view.findViewById(R.id.tv_refresh_state);
    }

    public void setTipsViewVisible(int visible){
        refreshState.setVisibility(visible);
    }

    @Override
    public void onPullDownState(float progress) {
        if (null != mRefreshTrigger){
            mRefreshTrigger.onPullDownState(progress);
        }
    }

    @Override
    public void onRefreshing() {
        if (null != mRefreshTrigger){
            mRefreshTrigger.onRefreshing();
        }
    }

    @Override
    public void onReleaseToRefresh() {
        if (null != mRefreshTrigger){
            mRefreshTrigger.onReleaseToRefresh();
        }
    }

    @Override
    public void onComplete() {
        if(refreshState.getVisibility() == VISIBLE){
            tipsIndex = mRandom.nextInt(sTIPS.length);
            refreshState.setText(sTIPS[tipsIndex]);
        }

        if (null != mRefreshTrigger){
            mRefreshTrigger.onComplete();
        }
    }

    @Override
    public void init() {
        if(refreshState.getVisibility() == VISIBLE){
            refreshState.setText(sTIPS[tipsIndex]);
        }

        if (null != mRefreshTrigger){
            mRefreshTrigger.init();
        }
    }
}
