package com.pds.ui.view.refresh.holder;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.core.view.ViewCompat;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;
import com.pds.ui.view.refresh.cb.BaseTied;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ZoomPullRefreshHolder extends PullRefreshHolder{
    private static final String TAG = "ZoomPullRefreshHolder";

    //手指下滑的距离超过这个高度就开始刷新数据
    private float mTotalDragDistance = 300;
    //下拉后回弹动画速率，数值越大，回弹越慢
    float mReplyRate = 3f;
    private int mOriginalViewHeight;

    public ZoomPullRefreshHolder(Context context, View refreshView) {
        super(context, refreshView);
    }

    @Override
    public boolean isRefreshing(float overScrollTop) {
        return overScrollTop > mTotalDragDistance;
    }

    private BaseTied convert() {
        return (BaseTied)mRefreshView;
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {
    }


    @Override
    public void setRefreshing(boolean refreshing) {
    }

    @Override
    public void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec) {
        super.measureChildAfter(parent, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void layoutChild(boolean changed, int left, int top, int right, int bottom) {
        if (null == mParent){
            return;
        }
        final int width = mParent.getMeasuredWidth();
        if(mRefreshView != null){
            int refreshWidth = mRefreshView.getMeasuredWidth();
            mRefreshView.layout((width/2 - refreshWidth/2), top, (width/2 + refreshWidth/2), mHeaderViewHeight);
        }
    }

    @Override
    public int getInitShowHeight() {
        return mOriginalViewHeight;
    }

    //下拉放大
    private void setZoom(float overScrollTop) {
        if (overScrollTop < 0){
            return;
        }
        ViewGroup.LayoutParams layoutParams = mRefreshView.getLayoutParams();

        int afterHeight = (int) (mOriginalViewHeight + overScrollTop);
        if (afterHeight - mOriginalViewHeight > 300)
            afterHeight = mOriginalViewHeight + 300;

        layoutParams.height = afterHeight;

        int position = (int) overScrollTop / 25;
        if (position >= 12)
            position = 12;

        mRefreshView.setLayoutParams(layoutParams);
        mRefreshView.getParent().requestLayout();
        mRefreshView.measure(layoutParams.width, layoutParams.height);
//        onNotifyParentReMesureListener.onMesure(layoutParams.width, layoutParams.height, position - 1);
    }

    //回弹缩小
    private void replyImage() {
        ViewGroup.LayoutParams layoutParams = mRefreshView.getLayoutParams();

        float distance = layoutParams.height - mOriginalViewHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distance, 0f).setDuration((long) (distance * mReplyRate));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoom((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();

    }

    @Override
    public void finishSpinner(float overScrollTop) {
        super.finishSpinner(overScrollTop);
        //没有请求数据的情况下，则在此回弹，请求数据的情况下则在请求完数据后，由外面activity回弹
        if (!mRefreshing){
            replyImage();
        }
    }

    @Override
    public void moveSpinner(float overScrollTop) {
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
        setZoom(overScrollTop);

        if (progress < 1.0f) {
            convert().onPullDownState(progress);
        } else {
            convert().onReleaseToRefresh();
        }

        final float translationY = overScrollTop > mTotalDragDistance ? mTotalDragDistance : overScrollTop;
        translateContentViews(translationY);
    }

    private void translateContentViews(float transY) {
        ensureTarget();
        Log.d(TAG,"translateContentViews:ty = "+ transY + " mHeaderViewHeight = "+ mHeaderViewHeight);
        if (mTarget != null) {
            final float maxValue = transY + mHeaderViewHeight;
            final float d = transY > maxValue ? maxValue : transY;
            mTarget.setTranslationY(d);
        }
    }
}
