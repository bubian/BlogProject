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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.core.view.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.OnChildScrollUpCallback;
import com.pds.ui.view.refresh.cb.OnRefreshListener;
import com.pds.ui.view.refresh.view.CircleImageRefreshView;

public class MultipleSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String LOG_TAG = "MSRL_TAG:";

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    /**
     * 布局里面添加的用于展示业务逻辑的UI组件
     */
    private View mTarget;
    OnRefreshListener mListener;
    private int mTouchSlop;
    private int mMediumAnimationDuration;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;

    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    /**
     * 下拉刷新展示的View在容器中位置索引
     */
    private int mRefreshViewIndex = -1;
    boolean mNotify;
    private OnChildScrollUpCallback mChildScrollUpCallback;
    /**
     * 下拉刷新展示View顶部偏移内容View顶部的距离
     */
    private int mCurrentTargetOffsetTop;
    private boolean mIsFirstMeasureSuccess = false;

    /**
     * 最开始下拉刷新展示View顶部偏移内容View顶部的距离
     */
    protected int mOriginalOffsetTop;


    void reset() {
        // todo
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

    public MultipleSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MultipleSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        mTotalDragDistance = mSpinnerOffsetEnd;

        addRefreshView();
        setChildrenDrawingOrderEnabled(true);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    private void addRefreshView(){
        mRefreshView = createRefreshView();
        addView(mRefreshView);
    }
    protected View createRefreshView() {
        return new CircleImageRefreshView(getContext());
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mRefreshViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mRefreshViewIndex;
        } else if (i >= mRefreshViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            mRefreshing = true;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd + mOriginalOffsetTop;
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            // todo
        }
    }

    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                // 刷新组件应该只包含内容View和下拉刷新显示的View
                if (!child.equals(mRefreshView)) {
                    mTarget = child;
                    break;
                }
            }
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

        // 测量内容View
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        // 测量下拉刷新组件
        measureChild(mRefreshView,widthMeasureSpec,heightMeasureSpec);

        mRefreshViewIndex = -1;
        // 找到下拉刷新View在容器中的位置。
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mRefreshView) {
                mRefreshViewIndex = index;
                break;
            }
        }
        if (!mIsFirstMeasureSuccess){
            mCurrentTargetOffsetTop = -mRefreshView.getMeasuredHeight();
            mOriginalOffsetTop = mCurrentTargetOffsetTop;
        }
        Log.d(LOG_TAG,"test:onMeasure:mCurrentTargetOffsetTop:"+ mCurrentTargetOffsetTop);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mIsFirstMeasureSuccess = true;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        if (getChildCount() == 0) { return;}
        if (mTarget == null) { ensureTarget(); }
        if (mTarget == null) { return; }

        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        int refreshWidth = mRefreshView.getMeasuredWidth();
        int refreshHeight = mRefreshView.getMeasuredHeight();
        Log.d(LOG_TAG,"test:onLayout:mCurrentTargetOffsetTop:"+ mCurrentTargetOffsetTop);
        mRefreshView.layout((width - refreshWidth) / 2, mCurrentTargetOffsetTop,
                (width  + refreshWidth ) / 2, mCurrentTargetOffsetTop+ refreshHeight);
    }

    void setTargetOffsetTopAndBottom(int offset) {
        mRefreshView.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshView, offset);
        mCurrentTargetOffsetTop = mRefreshView.getTop();
        Log.d(LOG_TAG,"test:setTargetOffsetTopAndBottom:mCurrentTargetOffsetTop:"+ mCurrentTargetOffsetTop);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        boolean canNotPullToRefresh = canNotPullToRefresh();
        Log.d(LOG_TAG,"onInterceptTouchEvent:canNotPullToRefresh:"+ canNotPullToRefresh);
        if (canNotPullToRefresh()) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mRefreshView.getTop());
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
                Log.d(LOG_TAG,"onInterceptTouchEvent:mIsBeingDragged:"+ mIsBeingDragged);
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
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void moveSpinner(float overScrollTop) {
        setHolderRefreshView();
        Log.i(LOG_TAG,"moveSpinner:overScrollTop="+overScrollTop);
        float slingshotDist = mUsingCustomStart ? mSpinnerOffsetEnd - mOriginalOffsetTop : mSpinnerOffsetEnd;
        mRefreshViewHolder.moveSpinner(overScrollTop,slingshotDist,mTotalDragDistance);


        float originalDragPercent = overScrollTop / mTotalDragDistance;
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));

        float extraOS = Math.abs(overScrollTop) - mTotalDragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        int t = targetY - mCurrentTargetOffsetTop;
        setTargetOffsetTopAndBottom(t);
    }

    @Override
    protected void finishSpinner(float overScrollTop) {
        Log.e("MU","finishSpinner:overScrollTop="+overScrollTop);
        if (overScrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */);
        } else {
            mRefreshing = false;
        }
        setHolderRefreshView();
        mRefreshViewHolder.setRefreshState(mRefreshing);
        float slingshotDist = mUsingCustomStart ? mSpinnerOffsetEnd - mOriginalOffsetTop : mSpinnerOffsetEnd;
        mRefreshViewHolder.finishSpinner(overScrollTop,slingshotDist,mTotalDragDistance);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex;

        Log.e(LOG_TAG,"onTouchEvent:mReturningToStart="+mReturningToStart);
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        boolean canNotPullToRefresh = canNotPullToRefresh();
        Log.e(LOG_TAG,"onTouchEvent:canNotPullToRefresh="+canNotPullToRefresh);
        if (canNotPullToRefresh()) {
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
                Log.e(LOG_TAG,"onTouchEvent:mIsBeingDragged="+mIsBeingDragged);
                if (mIsBeingDragged) {
                    final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    Log.e(LOG_TAG,"onTouchEvent:overScrollTop="+overScrollTop);
                    if (overScrollTop > 0) {
                        moveSpinner(overScrollTop);
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
                    final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overScrollTop);
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
                || mRefreshing || isNestedScrollInProgress() || mRefreshView == null;
    }

    @SuppressLint("NewApi")
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
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

    @Override
    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    /**
     *  设置刷新显示view
     * @param view
     */
    public void setRefreshView(View view) {
        if(view != null) {
            removeView(mRefreshView);
            this.mRefreshView = view;
            addView(view);
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }
}


