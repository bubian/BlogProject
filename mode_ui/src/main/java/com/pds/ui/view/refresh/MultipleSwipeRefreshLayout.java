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

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.OnChildScrollUpCallback;
import com.pds.ui.view.refresh.cb.OnRefreshListener;
import com.pds.ui.view.refresh.view.CircleImageRefreshView;
import com.pds.ui.view.refresh.holder.BaseHolder;

public class MultipleSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String LOG_TAG = MultipleSwipeRefreshLayout.class.getSimpleName();
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private View mTarget; // the target of the gesture
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

    private int mViewIndex = -1;
    boolean mNotify;
    private OnChildScrollUpCallback mChildScrollUpCallback;
    private int mCurrentTargetOffsetTop;


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
        if (mViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mViewIndex;
        } else if (i >= mViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
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
                if (!child.equals(mRefreshView)) {
                    mTarget = child;
                    break;
                }
            }
        }
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
        int refreshWidth = mRefreshView.getMeasuredWidth();
        int refreshHeight = mRefreshView.getMeasuredHeight();

        int currentTargetOffsetTop = getCurrentTargetOffsetTop();
        mRefreshView.layout((width / 2 - refreshWidth / 2) + 200, currentTargetOffsetTop,
                (width / 2 + refreshWidth / 2), currentTargetOffsetTop + refreshHeight);
    }

    private int getCurrentTargetOffsetTop(){
        if (mRefreshView instanceof ICover){
            mCurrentTargetOffsetTop =  ((ICover) mRefreshView).currentTargetOffsetTop();
        }
        return mCurrentTargetOffsetTop;
    }

    private int getViewDiameter(){
        if (mRefreshView instanceof ICover){
            return ((ICover) mRefreshView).viewDiameter();
        }
        return 0;
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
        int diameter = getViewDiameter();
        mRefreshView.measure(MeasureSpec.makeMeasureSpec(diameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(diameter, MeasureSpec.EXACTLY));
        mViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mRefreshView) {
                mViewIndex = index;
                break;
            }
        }
    }

    void setTargetOffsetTopAndBottom(int offset) {
        mRefreshView.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshView, offset);
        mCurrentTargetOffsetTop = mRefreshView.getTop();
    }

    protected int mOriginalOffsetTop;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (canNotPullToRefresh()) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mOriginalOffsetTop = mCurrentTargetOffsetTop = -getViewDiameter();
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
        Log.e("MU","moveSpinner:overScrollTop="+overScrollTop);
        mRefreshViewHolder.moveSpinner(overScrollTop);
    }

    @Override
    protected void finishSpinner(float overScrollTop) {
        Log.e("MU","finishSpinner:overScrollTop="+overScrollTop);
        if (overScrollTop > getTotalDragDistance()) {
            setRefreshing(true, true /* notify */);
        } else {
            mRefreshing = false;
            setHolderRefreshView();
            mRefreshViewHolder.finishSpinner(overScrollTop);
        }
    }

    private float getTotalDragDistance(){
        float totalDragDistance = -1;
        if (mRefreshView instanceof ICover){
            totalDragDistance =  ((ICover) mRefreshView).totalDragDistance();
        }
        return totalDragDistance;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

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
                if (mIsBeingDragged) {
                    final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
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


