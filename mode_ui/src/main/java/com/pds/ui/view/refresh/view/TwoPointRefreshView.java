package com.pds.ui.view.refresh.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.pds.ui.R;
import com.pds.ui.view.refresh.FrameAnimDrawable;
import com.pds.ui.view.refresh.cb.BaseTied;


import java.util.Random;

public class TwoPointRefreshView extends FrameLayout implements BaseTied {

    private static final int DEFAULT_HEADER_HEIGHT = 40;
    private final int mHeight;

    private ImageView loadingView;
    private TextView refreshState;

    FrameAnimDrawable drawable;

    static final String[] sTIPS = {
            "看前沿医疗资讯，上医联头条", "喜欢我们记得分享哟", "越用越懂你", "别忘了，右上方还有知识库等你发现"
    };

    private int tipsIndex;
    private Random mRandom;

    public TwoPointRefreshView(Context context) {
        this(context, null);
    }

    public TwoPointRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoPointRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mHeight = (int) (DEFAULT_HEADER_HEIGHT * metrics.density);
        setMinimumHeight(mHeight);
        setupViews();
        mRandom = new Random();
    }

    private void setupViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_medlinker_refresh_header, this);
        loadingView = view.findViewById(R.id.iv_loadingview);
        refreshState = view.findViewById(R.id.tv_refresh_state);

        int[] RES_IDS = new int[]{R.mipmap.loading_00000, R.mipmap.loading_00001, R.mipmap.loading_00002, R.mipmap.loading_00003, R.mipmap.loading_00004,
                R.mipmap.loading_00005, R.mipmap.loading_00006, R.mipmap.loading_00007, R.mipmap.loading_00008, R.mipmap.loading_00009,
                R.mipmap.loading_00010, R.mipmap.loading_00011, R.mipmap.loading_00012, R.mipmap.loading_00013, R.mipmap.loading_00014,
                R.mipmap.loading_00015, R.mipmap.loading_00016, R.mipmap.loading_00017, R.mipmap.loading_00018, R.mipmap.loading_00019,
                R.mipmap.loading_00020, R.mipmap.loading_00021, R.mipmap.loading_00022, R.mipmap.loading_00023, R.mipmap.loading_00024,
                R.mipmap.loading_00025, R.mipmap.loading_00026, R.mipmap.loading_00027, R.mipmap.loading_00028, R.mipmap.loading_00029,
                R.mipmap.loading_00030, R.mipmap.loading_00031, R.mipmap.loading_00032, R.mipmap.loading_00033, R.mipmap.loading_00034,
                R.mipmap.loading_00035, R.mipmap.loading_00036, R.mipmap.loading_00037, R.mipmap.loading_00038, R.mipmap.loading_00039,
                R.mipmap.loading_00040, R.mipmap.loading_00041, R.mipmap.loading_00042, R.mipmap.loading_00043, R.mipmap.loading_00044
        };

        drawable = new FrameAnimDrawable(23, RES_IDS, getResources());
        loadingView.setImageDrawable(drawable);
    }

    public void setTipsViewVisible(int visible){
        refreshState.setVisibility(visible);
    }

    @Override
    public void onPullDownState(float progress) {
        drawable.stop();
        drawable.invalidate(Math.round(progress * (drawable.getFrameCount() - 1)));
    }

    @Override
    public void onRefreshing() {
        drawable.start();
    }

    @Override
    public void onReleaseToRefresh() {
        drawable.start();
    }

    @Override
    public void onComplete() {
        drawable.stop();
        if(refreshState.getVisibility() == VISIBLE){
            tipsIndex = mRandom.nextInt(sTIPS.length);
            refreshState.setText(sTIPS[tipsIndex]);
        }
    }

    @Override
    public void init() {
        drawable.stop();
        if(refreshState.getVisibility() == VISIBLE){
            refreshState.setText(sTIPS[tipsIndex]);
        }
    }

    @Override
    public void finishSpinner(float overScrollTop, float slingshotDist, float totalDragDistance) {

    }

    @Override
    public void moveSpinner(float overScrollTop, float slingshotDist, float totalDragDistance) {

    }

    @Override
    public void setRefreshState(boolean isRefreshing) {

    }

    @Override
    public void reset() {

    }
}
