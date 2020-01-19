package com.pds.ui.view.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomSwipeToRefresh extends MultipleSwipeRefreshLayout {
    private final int mTouchSlop;
    private float mPrevX;
    private float mPrevY;
    private boolean isViewPagerDragger;

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mPrevY = MotionEvent.obtain(event).getY();
                isViewPagerDragger = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(isViewPagerDragger){
                    return false;
                }
                final float eventX = event.getX();
                final float eventY = event.getY();
                float xDiff = Math.abs(eventX - mPrevX);
                float yDiff = Math.abs(eventY - mPrevY);

                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    isViewPagerDragger = true;
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                isViewPagerDragger = false;
                break;
        }

        return super.onInterceptTouchEvent(event);
    }
}

