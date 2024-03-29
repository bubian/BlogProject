package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;
import com.pds.ui.view.refresh.cb.BaseCover;
import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.ISpinnerAction;
import com.pds.ui.view.refresh.cb.RefreshState;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class CoverPullRefreshHolder extends BaseHolder{
    private static final String TAG = "CPRH_TAG:CPRH:";
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int SCALE_DOWN_DURATION = 150;

    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    protected int mFrom;
    private boolean mRefreshing;

    // 是否开启了自定义开始位置
    boolean mUsingCustomStart;
    protected int mSpinnerOffsetEnd;
    protected float mTotalDragDistance;
    private boolean mIsFirstMeasureSuccess;
    boolean mScale;
    // 下拉刷新展示View顶部偏移内容View顶部的距离
    private int mCurrentTargetOffsetTop;
    //最开始下拉刷新展示View顶部偏移内容View顶部的距离
    protected int mOriginalOffsetTop;

    private Animation mScaleDownAnimation;
    private Animation mScaleAnimation;
    private int mMediumAnimationDuration;
    private int mCustomSlingshotDistance;

    public CoverPullRefreshHolder(@NonNull Context context, View refreshView) {
        super(context, refreshView);
        mContext = context;
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        mTotalDragDistance = mSpinnerOffsetEnd;

        mMediumAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        moveToStart(1.0f);
    }

    private void syncRefreshState(boolean isRefreshing) {
        mRefreshing = isRefreshing;
        convert().setRefreshState(mRefreshing);
    }

    private BaseCover convert(){
       return  (BaseCover)mRefreshView;
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mRefreshView.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    private void setAnimationProgress(float progress) {
        mRefreshView.setScaleX(progress);
        mRefreshView.setScaleY(progress);
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        mRefreshView.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshView, offset);
        mCurrentTargetOffsetTop = mRefreshView.getTop();
    }

    @Override
    public void reset() {
        mRefreshView.clearAnimation();
        mRefreshView.setVisibility(View.GONE);
        convert().reset();
        if (convert().doCustomExitAnimation()){
            mCurrentTargetOffsetTop =mRefreshView.getTop();
            return;
        }
        if (mScale) {
            // 缩放消失
            setAnimationProgress(0);
        }else {
            // 平移到开始位置
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
        }
        mCurrentTargetOffsetTop =mRefreshView.getTop();
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd - Math.abs(mOriginalOffsetTop);
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mRefreshView.getTop();
            setTargetOffsetTopAndBottom(offset);
            ((ICover)mRefreshView).applyTransformation(interpolatedTime,t);
        }
    };

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG,"test:onAnimationEnd:mRefreshing= "+ mRefreshing);
            if (mRefreshing) {
                mParent.doNotify(RefreshState.START);
                mCurrentTargetOffsetTop = mRefreshView.getTop();
            } else {
                reset();
            }
            convert().onAnimationEnd(animation);
        }
    };

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            if (mRefreshView instanceof ICover){
                ((ICover) mRefreshView).applyTransformation(interpolatedTime,t);
            }
            moveToStart(interpolatedTime);
        }

    };

    private void startScaleDownAnimation() {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                if (!convert().doComplete(interpolatedTime, t)){
                    mRefreshView.setScaleX(1 - interpolatedTime);
                    mRefreshView.setScaleY(1 - interpolatedTime);
                }
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mScaleDownAnimation.setAnimationListener(mRefreshListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mScaleDownAnimation);
    }


    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mRefreshView.setVisibility(View.VISIBLE);
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mScaleAnimation);
    }

    private void animateOffsetToCorrectPosition(int from) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from,Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(listener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    @Override
    public void finishSpinner(float overScrollTop) {
        float slingshotDist = mUsingCustomStart ? mSpinnerOffsetEnd - mOriginalOffsetTop : mSpinnerOffsetEnd;
        if (mRefreshView instanceof ISpinnerAction){
            ((ISpinnerAction) mRefreshView).finishSpinner(overScrollTop, slingshotDist, mTotalDragDistance);
        }
        Animation.AnimationListener listener = null;
        if (!mScale) {
            listener = new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!mScale) {
                        startScaleDownAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            };
        }
        animateOffsetToStartPosition(mCurrentTargetOffsetTop,listener);
    }

    @Override
    public void moveSpinner(float overScrollTop) {
        if (null == mRefreshView){
            return;
        }
        Log.e(TAG,"moveSpinner:overScrollTop="+overScrollTop);
        if (mRefreshView.getVisibility() != View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE);
        }

        float slingshotDist = mCustomSlingshotDistance > 0
                ? mCustomSlingshotDistance
                : (mUsingCustomStart
                ? mSpinnerOffsetEnd - mOriginalOffsetTop
                : mSpinnerOffsetEnd);

        if (mRefreshView instanceof ISpinnerAction){
            ((ISpinnerAction) mRefreshView).moveSpinner(overScrollTop,slingshotDist,mTotalDragDistance);
        }

        float originalDragPercent = overScrollTop / mTotalDragDistance;
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));

        float extraOS = Math.abs(overScrollTop) - mTotalDragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        int t = targetY - mCurrentTargetOffsetTop;

        Log.e(TAG,"moveSpinner:t="+t);
        setTargetOffsetTopAndBottom(t);
    }

    @Override
    public void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec){
        mParent = parent;
        if (!mIsFirstMeasureSuccess){
            mCurrentTargetOffsetTop = -mRefreshView.getMeasuredHeight();
            mOriginalOffsetTop = mCurrentTargetOffsetTop;
        }
    }

    @Override
    public void layoutChild(boolean changed, int left, int top, int right, int bottom) {
        mIsFirstMeasureSuccess = true;
        if (null == mParent){
            return;
        }
        final int width = mParent.getMeasuredWidth();
        int refreshWidth = mRefreshView.getMeasuredWidth();
        int refreshHeight = mRefreshView.getMeasuredHeight();
        mRefreshView.layout((width - refreshWidth) / 2, mCurrentTargetOffsetTop,
                (width  + refreshWidth ) / 2, mCurrentTargetOffsetTop+ refreshHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN){
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mRefreshView.getTop());
        }
        return true;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd + mOriginalOffsetTop;
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            if (!convert().doCustomEnterAnimation()){
                setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
                startScaleUpAnimation(mRefreshListener);
            }
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop);
            } else {
                startScaleDownAnimation();
            }
    }

    public void setProgressViewOffset(boolean scale, int start, int end) {
        if (mRefreshing){
            return;
        }
        mScale = scale;
        mOriginalOffsetTop = start;
        mSpinnerOffsetEnd = end;
        mUsingCustomStart = true;
        reset();
    }

    @Override
    public void setRefreshState(boolean mRefreshing) {
        syncRefreshState(mRefreshing);
    }

    public void setSlingshotDistance(@Px int slingshotDistance) {
        mCustomSlingshotDistance = slingshotDistance;
    }

    @Override
    public boolean isRefreshing(float overScrollTop) {
        return overScrollTop > mTotalDragDistance;
    }
}
