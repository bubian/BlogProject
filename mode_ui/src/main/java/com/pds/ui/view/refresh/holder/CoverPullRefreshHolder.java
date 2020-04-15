package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.core.view.ViewCompat;

import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.ISpinnerAction;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class CoverPullRefreshHolder extends BaseHolder{
    private static final String TAG = "MSRL_TAG:CPRH:";
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    protected int mFrom;
    private int mViewDiameter;
    protected int mOriginalOffsetTop;

    private int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;
    private boolean mIsRefreshing;


    public CoverPullRefreshHolder(Context context) {
        this(context,null);
    }

    public CoverPullRefreshHolder(Context context, View refreshView) {
        super(context, refreshView);
        if (null == refreshView){
            return;
        }
        if (null == mContext){
            mContext = refreshView.getContext();
        }

        mMediumAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        mViewDiameter = viewDiameter();
        mOriginalOffsetTop = mCurrentTargetOffsetTop = - mViewDiameter;
        moveToStart(1.0f);
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mRefreshView.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    void setTargetOffsetTopAndBottom(int offset) {
        mRefreshView.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshView, offset);
        mCurrentTargetOffsetTop = mRefreshView.getTop();
    }

    int viewDiameter(){
        if (mRefreshView instanceof ICover){
            return ((ICover)mRefreshView).viewDiameter();
        }
        return 0;
    }

    @Override
    public void finishSpinner(float overScrollTop,float slingshotDist,float totalDragDistance) {
        Log.e("MU::::","Override:overScrollTop="+overScrollTop);
        if (mRefreshView instanceof ISpinnerAction){
            ((ISpinnerAction) mRefreshView).finishSpinner(overScrollTop, slingshotDist, totalDragDistance);
        }

        if (!mIsRefreshing){
            animateOffsetToStartPosition(mCurrentTargetOffsetTop);
        }
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            if (mRefreshView instanceof ICover){
               ((ICover) mRefreshView).applyTransformation(interpolatedTime,t);
            }
            moveToStart(interpolatedTime);
        }
    };

    private void animateOffsetToStartPosition(int from) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    @Override
    public void moveSpinner(float overScrollTop, float slingshotDist, float totalDragDistance) {
        if (null == mRefreshView){
            return;
        }
        Log.e(TAG,"moveSpinner:overScrollTop="+overScrollTop);
        if (mRefreshView.getVisibility() != View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE);
        }

        if (mRefreshView instanceof ISpinnerAction){
            ((ISpinnerAction) mRefreshView).moveSpinner(overScrollTop,slingshotDist,totalDragDistance);
        }
    }

    @Override
    public void setRefreshState(boolean isRefreshing) {
        mIsRefreshing = isRefreshing;
        ((ISpinnerAction) mRefreshView).setRefreshState(mIsRefreshing);
    }
}
