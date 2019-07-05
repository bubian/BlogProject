/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pds.ui.view.refresh;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

/**
 * 修改官方SwipeRefreshLayout源码自定义刷新控件
 * 主要修改：
 * 1、内置默认下拉效果
 * 2、允许添加自定义的下拉效果
 *
 * @author 彭道松
 * @date 2019/1/7
 */

public class ISwipeRefreshLayout extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {

    @VisibleForTesting
    private static final int DEFAULT_HEADER_HEIGHT = 40;
    private final int HEADER_VIEW_MIN_HEIGHT;

    private static final String LOG_TAG = ISwipeRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 200;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the headerview should stop
    private static final int DEFAULT_HEADER_TARGET = 64;

    private View mTarget; // the target of the gesture
    OnRefreshListener mListener;
    boolean mRefreshing = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1;


    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private int mMediumAnimationDuration;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    boolean mScale;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private View mRefreshView;

    //    CircleImageView mCircleView;
    private int mCircleViewIndex = -1;

    float mStartingScale;

    protected int mOriginalOffsetTop;

    private Animation mScaleDownAnimation;

    private Animation mScaleDownToStartAnimation;

    boolean mNotify;

    private int mHeaderViewHeight;

    // Whether the client has set a custom starting position;
    boolean mUsingCustomStart;

    private OnChildScrollUpCallback mChildScrollUpCallback;


    private ImageView headerBgImageView;
    private int mViewWidth ;
    private int mViewHeight ;
    private int firstPosition;
    private OnNotifyParentReMesureListener onNotifyParentReMesureListener;

    private float mGroupTotalDragDistance = 300; //手指下滑的距离超过这个高度就开始刷新数据
    float mReplyRate = 3f; //下拉后回弹动画速率，数值越大，回弹越慢

    public void setOnNotyfiParentReMesureListener(OnNotifyParentReMesureListener onNotifyParentReMesureListener) {
        this.onNotifyParentReMesureListener = onNotifyParentReMesureListener;
    }

    public void setHeaderBgImageView(ImageView headerBgImageView) {
        this.headerBgImageView = headerBgImageView;
        LayoutParams layoutParams = headerBgImageView.getLayoutParams();

        mViewWidth = layoutParams.width;
        mViewHeight = layoutParams.height;
    }

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
                getRefreshTrigger().onRefreshing();
                if (mNotify) {
                    if (mListener != null) {
//                        System.out.println(" onRefresh !!!");
                        mListener.onRefresh();  //在这儿通知的界面开始更新数据
                    }
                }
            } else {
                reset();
            }
        }
    };

    void reset() {
        mRefreshView.clearAnimation();
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            translateContentViews(0f);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /**
     * Simple constructor to use when creating a ISwipeRefreshLayout from code.
     *
     * @param context
     */
    public ISwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating ISwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public ISwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mHeaderViewHeight = (int) (DEFAULT_HEADER_HEIGHT * metrics.density);
        HEADER_VIEW_MIN_HEIGHT = mHeaderViewHeight;
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mTotalDragDistance = (int) (DEFAULT_HEADER_TARGET * metrics.density);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        //add default refreshview
        setRefreshHeaderView(new MedlinkerRefreshHeaderView(getContext()));
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndex;
        } else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    /**
     * @param view
     */
    public void setRefreshHeaderView(View view) {
        if(view == null){
            return;
        }
        removeView(mRefreshView);
        this.mRefreshView = view;
        view.setMinimumHeight(HEADER_VIEW_MIN_HEIGHT);
        addView(view);
        getRefreshTrigger().init();
    }


    public void setGroupRefreshHeaderView(View view) {
        if(view == null){
            return;
        }
        removeView(mRefreshView);
        this.mRefreshView = view;
        view.setMinimumHeight(0);
        addView(view);
        getRefreshTrigger().init();
    }
    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
//        System.out.println("setRefreshing refreshing "+refreshing);
        if(mRefreshView == null || !isEnabled()){
            return;
        }
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            //移动到刷新位置
            animateOffsetToCorrectPosition(mRefreshListener);
            mNotify = false;
            startScaleUpAnimation();
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

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

    /**
     * Pre API 11, this does an alpha animation.
     *
     * @param progress
     */
    void setAnimationProgress(float progress) {
        mRefreshView.setScaleX(progress);
        mRefreshView.setScaleY(progress);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition( mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
                translateContentViews((mTarget.getTranslationY() * (1 - interpolatedTime)));
//                System.out.println(" mScaleDownAnimation interpolatedTime = " + interpolatedTime);
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
            getRefreshTrigger().onComplete();
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mRefreshView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        if(mRefreshView != null){
            int refreshWidth = mRefreshView.getMeasuredWidth();
            mRefreshView.layout((width/2 - refreshWidth/2), childTop - mHeaderViewHeight, (width/2 + refreshWidth/2), childTop);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        if(mRefreshView != null){
            measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec);
            updateBaseValues(mRefreshView.getMeasuredHeight());
            mCircleViewIndex = -1;
            // Get the index of the circleview.
            for (int index = 0; index < getChildCount(); index++) {
                if (getChildAt(index) == mRefreshView) {
                    mCircleViewIndex = index;
                    break;
                }
            }
        }
    }

    // 更新基础信息
    private void updateBaseValues(int refreshViewHeight) {
        mHeaderViewHeight = refreshViewHeight;
        mOriginalOffsetTop = -mHeaderViewHeight;
        setDistanceToTriggerSync(Math.round(mHeaderViewHeight * 1.5f));
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    /**
     * Set a callback to override {@link ISwipeRefreshLayout#canChildScrollUp()} method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     *
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

//        System.out.println("isEnabled "+ isEnabled()+"; mReturningToStart "+mReturningToStart+"; canChildScrollUp() "+canChildScrollUp()+" " +
//                " mRefreshing "+mRefreshing+"; mNestedScrollInProgress "+mNestedScrollInProgress+" ; mRefreshView "+mRefreshView);
        if (canNotPullToRefresh()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                translateContentViews(0f);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            mRefreshView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    @SuppressLint("NewApi")
    private void moveSpinner(float overscrollTop) {
        // where 1.0f is a full circle
        if (mRefreshView.getVisibility() != View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            mRefreshView.setScaleX(1f);
            mRefreshView.setScaleY(1f);
        }

        Log.d("overscrollTop","overscrollTop="+overscrollTop + " mGroupTotalDragDistance = "+mGroupTotalDragDistance);

        if (mScale) {
            if (null!=headerBgImageView)
                setAnimationProgress(Math.min(1f, overscrollTop / mGroupTotalDragDistance));
            else
                setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
        }
        float progress;
        if (null!=headerBgImageView){
            progress = overscrollTop / mGroupTotalDragDistance;
            setZoom(overscrollTop);
        } else{
            progress = overscrollTop / mTotalDragDistance;
        }

        if (progress <1.0f) {
            getRefreshTrigger().onPullDownState(progress);
        } else {
            getRefreshTrigger().onReleaseToRefresh();
        }
        final float tranlationY = overscrollTop > mTotalDragDistance ? mTotalDragDistance : overscrollTop;
        translateContentViews(tranlationY);
    }

    //下拉放大
    private void setZoom(float progress){

        if (progress<0)
            return;
        LayoutParams layoutParams = headerBgImageView.getLayoutParams();

        int afterHeight = (int)(mViewHeight+progress);
        if (afterHeight-mViewHeight>300)
            afterHeight = mViewHeight+300;

        layoutParams.height = afterHeight;

        Log.d("ISwipeRefreshLayout","progress="+progress);

        int position = (int)progress/25;
        Log.d("ISwipeRefreshLayout","position="+position);
        if (position>=12)
            position = 12;

        headerBgImageView.setLayoutParams(layoutParams);

        Log.d("ISwipeRefreshLayout","orignalHeight="+mViewHeight);
        Log.d("ISwipeRefreshLayout","zoomHeight="+headerBgImageView.getLayoutParams().height);
        headerBgImageView.getParent().requestLayout();
        headerBgImageView.measure(layoutParams.width,layoutParams.height);
        onNotifyParentReMesureListener.onMesure(layoutParams.width,layoutParams.height,position-1);
    }



    //回弹缩小
    private void replyImage(){
        LayoutParams layoutParams = headerBgImageView.getLayoutParams();

        float distance = layoutParams.height - mViewHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distance, 0f).setDuration((long) (distance * mReplyRate));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoom((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();

    }


    private IRefreshTrigger getRefreshTrigger() {
        try {
            return ((IRefreshTrigger) mRefreshView);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(" mRefreshHeadview must implements IRefreshTrigger !!!");
        }
    }


    //
    private void finishSpinner(float overscrollTop) {
        if (null!=headerBgImageView){
            if (overscrollTop > mGroupTotalDragDistance){
                setRefreshing(true, true /* notify */);
            }

            else{
                // cancel refresh
                mRefreshing = false;
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

            //没有请求数据的情况下，则在此回弹，请求数据的情况下则在请求完数据后，由外面activity回弹
            if (!isRefreshing())
                replyImage();

        }else{

            if (overscrollTop > mTotalDragDistance) {
                setRefreshing(true, true /* notify */);
            } else {

                // cancel refresh
                mRefreshing = false;
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

        }





    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (canNotPullToRefresh()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop);

                        if (null!=headerBgImageView)
                            isGroupRefreshed = false;

                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    /**
     * @return 不能下拉刷新状态
     */
    private boolean canNotPullToRefresh() {
        return !isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress || mRefreshView == null;
    }

    @SuppressLint("NewApi")
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
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

    private void translateContentViews(float transY) {
        if(mRefreshView != null){
            mRefreshView.bringToFront();
            ViewCompat.setTranslationY(mRefreshView, transY);
        }

        //mTarget translationY offset
        if (mTarget != null) {
            final float maxValue = mRefreshView.getTranslationY() + mHeaderViewHeight;
            ViewCompat.setTranslationY(mTarget, transY > maxValue ? maxValue : transY);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    /**
     * Classes that wish to override {@link ISwipeRefreshLayout#canChildScrollUp()} method
     * behavior should implement this interface.
     */
    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link ISwipeRefreshLayout#canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent ISwipeRefreshLayout that this callback is overriding.
         * @param child  The child view of ISwipeRefreshLayout.
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(ISwipeRefreshLayout parent, @Nullable View child);
    }


    public interface OnNotifyParentReMesureListener{
        void onMesure(int width, int heigth, int position);
    }

    private boolean isGroupRefreshing = false;
    private boolean isGroupRefreshed = false; //用于判断是否已经请求过数据了

    public boolean isGroupRefreshing() {
        return isGroupRefreshing;
    }

    public void setGroupRefreshing(boolean groupRefreshing) {
        isGroupRefreshing = groupRefreshing;
    }

    public boolean isGroupRefreshed() {
        return isGroupRefreshed;
    }

    public void setGroupRefreshed(boolean groupRefreshed) {
        isGroupRefreshed = groupRefreshed;
    }

}


