package com.pds.ui.view.refresh.view;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:25
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pds.ui.R;
import com.pds.ui.view.refresh.IOSLoadingView;
import com.pds.ui.view.refresh.cb.BaseTied;
import com.pds.ui.view.refresh.cb.IRefreshTrigger;
import com.pds.util.device.DeviceUtil;
import com.pds.util.unit.UnitConversionUtils;

import java.util.Random;

public class ZoomRefreshHeaderView extends FrameLayout implements BaseTied {

    private ImageView mZoomBg;
    private ImageView mZoomMongolian;
    private IOSLoadingView mZoomIosView;
    private TextView mZoomRefreshState;

    static final String[] sTIPS = {
            "看前沿医疗资讯，上医联头条", "喜欢我们记得分享哟", "越用越懂你", "别忘了，右上方还有知识库等你发现"
    };

    private int tipsIndex;
    private Random mRandom;

    public ZoomRefreshHeaderView(Context context) {
        this(context, null);
    }

    public ZoomRefreshHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomRefreshHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
        mRandom = new Random();
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UnitConversionUtils.dip2px(getContext(),200));
        setLayoutParams(params);
    }
    private void setupViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.header_zoom, this);
        mZoomBg = view.findViewById(R.id.zoom_bg);
        mZoomMongolian = view.findViewById(R.id.zoom_mongolian);
        mZoomIosView = view.findViewById(R.id.zoom_ios_loading);
        mZoomRefreshState = view.findViewById(R.id.zoom_state);
    }

    @Override
    public void init() {
        if(mZoomRefreshState.getVisibility() == VISIBLE){
            mZoomRefreshState.setText(sTIPS[tipsIndex]);
        }
    }

    private int h(){
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public void setTipsViewVisible(int visible){
        mZoomRefreshState.setVisibility(visible);
    }

    @Override
    public void onPullDownState(float progress) {

    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onReleaseToRefresh() {

    }

    @Override
    public void onComplete() {
        if(mZoomRefreshState.getVisibility() == VISIBLE){
            tipsIndex = mRandom.nextInt(sTIPS.length);
            mZoomRefreshState.setText(sTIPS[tipsIndex]);
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
