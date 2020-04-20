package com.pds.ui.view.refresh.holder;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.core.view.ViewCompat;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;
import com.pds.ui.view.refresh.cb.BaseTied;
import com.pds.ui.view.refresh.cb.ISpinnerAction;
import com.pds.ui.view.refresh.cb.RefreshState;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:46
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PullRefreshHolder extends BaseHolder {

    private static final String TAG = "PullRefreshHolder";
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private boolean mRefreshing;
    private int mHeaderViewHeight;
    private boolean mScale;
    private float mTotalDragDistance;
    private View mTarget;
    private int mMediumAnimationDuration;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private int mOriginalOffsetTop;

    public PullRefreshHolder(Context context, View refreshView) {
        super(context,refreshView);
    }


    @Override
    public void init() {
        convert().init();
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mTotalDragDistance = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        mMediumAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
    }

    @Override
    public void reset() {
        mRefreshView.clearAnimation();
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            translateContentViews(0f);
        }
    }

    private BaseTied convert(){
        return  (BaseTied)mRefreshView;
    }


    private void syncRefreshState(boolean isRefreshing) {
        mRefreshing = isRefreshing;
        convert().setRefreshState(mRefreshing);
    }

    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    private void animateOffsetToCorrectPosition(Animation.AnimationListener listener) {
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mAnimateToCorrectPosition.setAnimationListener(listener);
        }
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (null == mRefreshView){
            return;
        }
        //移动到刷新位置
        animateOffsetToCorrectPosition(mRefreshListener);
        startScaleUpAnimation();
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {
        if (mRefreshing) {
            animateOffsetToCorrectPosition( mRefreshListener);
        } else {
            startScaleDownAnimation(mRefreshListener);
        }
    }

    // 更新基础信息
    private void updateBaseValues(int refreshViewHeight) {
        mHeaderViewHeight = refreshViewHeight;
        mOriginalOffsetTop = -mHeaderViewHeight;
        setDistanceToTriggerSync(Math.round(mHeaderViewHeight * 1.5f));
    }

    /**
     * 回到刷新位置的动画
     */
    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            final float translationY = mRefreshView.getTranslationY() + (mHeaderViewHeight - mRefreshView.getTranslationY()) * interpolatedTime;
            translateContentViews(translationY);
        }
    };

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @SuppressLint("NewApi")
        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                convert().onRefreshing();
                mParent.doNotify(RefreshState.START);
            } else {
                reset();
            }

//            convert().onAnimationEnd(animation);
        }
    };

    @SuppressLint("NewApi")
    private void startScaleUpAnimation() {
        mRefreshView.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setAnimationProgress(((float) animation.getAnimatedValue()));
//                listener.onAnimationEnd(null);
            }
        });
        animator.setDuration(mMediumAnimationDuration);
        animator.start();
    }

    @Override
    public void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec) {
        mParent = parent;
        updateBaseValues(mRefreshView.getMeasuredHeight());
    }


    @Override
    public void layoutChild(boolean changed, int left, int top, int right, int bottom) {
        if (null == mParent){
            return;
        }
        final int width = mParent.getMeasuredWidth();
        if(mRefreshView != null){
            int refreshWidth = mRefreshView.getMeasuredWidth();
            mRefreshView.layout((width/2 - refreshWidth/2), top - mHeaderViewHeight, (width/2 + refreshWidth/2), top);
        }
    }

    @Override
    public void setRefreshState(boolean isRefreshing) {
        syncRefreshState(isRefreshing);
    }

    void setAnimationProgress(float progress) {
        mRefreshView.setScaleX(progress);
        mRefreshView.setScaleY(progress);
    }

    @Override
    public void moveSpinner(float overScrollTop) {
        Log.d(TAG,"moveSpinner:overScrollTop = "+ overScrollTop);
        if (mRefreshView.getVisibility() != View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            mRefreshView.setScaleX(1f);
            mRefreshView.setScaleY(1f);
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overScrollTop / mTotalDragDistance));
        }
        float progress = overScrollTop / mTotalDragDistance;
        if (progress < 1.0f) {
            convert().onPullDownState(progress);
        } else {
            convert().onReleaseToRefresh();
        }

        final float translationY = overScrollTop > mTotalDragDistance ? mTotalDragDistance : overScrollTop;
        translateContentViews(translationY);
    }

    @Override
    public void finishSpinner(float overScrollTop) {
        if (mRefreshView instanceof ISpinnerAction){
//            ((ISpinnerAction) mRefreshView).finishSpinner(overScrollTop, slingshotDist, mTotalDragDistance);
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
                        startScaleDownAnimation(null);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            };
        }
        animateOffsetToStartPosition(listener);
    }

    private void translateContentViews(float transY) {
        if(mRefreshView != null){
            mRefreshView.bringToFront();
            mRefreshView.setTranslationY(transY);
        }
        ensureTarget();
        if (mTarget != null) {
            final float maxValue = mRefreshView.getTranslationY() + mHeaderViewHeight;
            mTarget.setTranslationY(transY > maxValue ? maxValue : transY);
        }
    }

    private void ensureTarget() {
        if (null == mParent){
            return;
        }
        if (mTarget == null) {
            for (int i = 0; i < mParent.getChildCount(); i++) {
                View child = mParent.getChildAt(i);
                // 刷新组件应该只包含内容View和下拉刷新显示的View
                if (!child.equals(mRefreshView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    float mStartingScale;
    private Animation mScaleDownToStartAnimation;
    private static final int SCALE_DOWN_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    void moveToStart(float interpolatedTime) {
        translateContentViews(mRefreshView.getTranslationY() * (1 - interpolatedTime));
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    @SuppressLint("NewApi")
    private void startScaleDownReturnToStartAnimation(Animation.AnimationListener listener) {
        mStartingScale = mRefreshView.getScaleX();
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mScaleDownToStartAnimation.setAnimationListener(listener);
        }
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mScaleDownToStartAnimation);
    }

    private void animateOffsetToStartPosition( Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(listener);
        } else {
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mAnimateToStartPosition.setAnimationListener(listener);
            }
            mRefreshView.clearAnimation();
            mRefreshView.startAnimation(mAnimateToStartPosition);
        }
    }

    @Override
    public boolean isRefreshing(float overScrollTop) {
        return overScrollTop > mTotalDragDistance;
    }

    private Animation mScaleDownAnimation;
    void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
                translateContentViews((mTarget.getTranslationY() * (1 - interpolatedTime)));
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mScaleDownAnimation.setAnimationListener(listener);
        //刷新完成，延时200ms，用于展示完成状态.但最好是有一个完成的动画。TODO
        mScaleDownAnimation.setStartOffset(listener != null ? 500 : -1);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mScaleDownAnimation);

        if (listener != null) {
            //刷新完成
           convert().onComplete();
        }
    }
}

