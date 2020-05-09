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
import com.pds.ui.view.refresh.holder.CoverPullRefreshHolder;
import com.pds.ui.view.refresh.holder.PullRefreshHolder;
import com.pds.ui.view.refresh.holder.ZoomPullRefreshHolder;
import com.pds.ui.view.refresh.view.CircleImageRefreshView;
import com.pds.ui.view.refresh.view.TwoPointRefreshView;
import com.pds.ui.view.refresh.view.ZoomRefreshHeaderView;

public class MultipleSwipeRefreshLayout extends BaseSwipeRefreshLayout {

    private static final String LOG_TAG = "MSRL_TAG:";

    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private int mTouchSlop;
    private int mMediumAnimationDuration;
    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    public MultipleSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MultipleSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
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
        checkRefreshHolder();
        mRefreshViewHolder.init();
    }

    protected View createRefreshView() {
        if (mRefreshType == PULL_TYPE){
            return new TwoPointRefreshView(getContext());
        }else if (mRefreshType == ZOOM_TYPE){
            return new ZoomRefreshHeaderView(getContext());
        }else {
            return new CircleImageRefreshView(getContext());
        }
    }

    private void syncRefreshState(boolean refreshing){
        mRefreshing = refreshing;
        mRefreshViewHolder.setRefreshState(mRefreshing);
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            syncRefreshState(true);
            mRefreshViewHolder.setRefreshing(true);
            mNotify = false;
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            syncRefreshState(refreshing);
            mRefreshViewHolder.setRefreshing(refreshing,notify);
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
        checkRefreshHolder();
        mRefreshViewIndex = -1;
        // 找到下拉刷新View在容器中的位置。
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mRefreshView) {
                mRefreshViewIndex = index;
                break;
            }
        }
        mRefreshViewHolder.measureChildAfter(this,widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
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
        child.layout(childLeft, childTop + mRefreshViewHolder.getInitShowHeight(), childLeft + childWidth, childTop + childHeight);
        checkRefreshHolder();
        if (mRefreshType == PULL_TYPE){
            mRefreshViewHolder.layoutChild(changed,left,childTop,right,bottom);
        }else {
            mRefreshViewHolder.layoutChild(changed,left,top,right,bottom);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @SuppressLint("NewApi")
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
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
        if (canNotPullToRefresh) {
            return false;
        }

        mRefreshViewHolder.onInterceptTouchEvent(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
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

    @Override
    protected void moveSpinner(float overScrollTop) {
        checkRefreshHolder();
        mRefreshViewHolder.moveSpinner(overScrollTop);
    }

    @Override
    protected void finishSpinner(float overScrollTop) {
        checkRefreshHolder();
        if (mRefreshViewHolder.isRefreshing(overScrollTop)) {
            setRefreshing(true, true /* notify */);
        } else {
            syncRefreshState(false);
            mRefreshViewHolder.finishSpinner(overScrollTop);
        }
    }
}

