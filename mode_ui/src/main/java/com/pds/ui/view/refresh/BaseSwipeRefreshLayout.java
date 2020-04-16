package com.pds.ui.view.refresh;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.pds.ui.view.refresh.cb.OnRefreshListener;
import com.pds.ui.view.refresh.cb.RefreshState;
import com.pds.ui.view.refresh.holder.BaseHolder;
import com.pds.ui.view.refresh.holder.CoverPullRefreshHolder;
import com.pds.ui.view.refresh.holder.PullRefreshHolder;
import com.pds.ui.view.refresh.holder.ZoomPullRefreshHolder;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:10
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public abstract class BaseSwipeRefreshLayout extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {

    private static final String TAG = "BSRL:";
    protected Context mContext;
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;

    private boolean mNestedScrollInProgress;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    /**
     * 下拉刷新展示的View在容器中位置索引
     */
    protected int mRefreshViewIndex = -1;

    /**
     * 下拉刷新时，显示的View
     */
    protected View mRefreshView;
    protected View mTarget;
    protected boolean mReturningToStart;
    protected boolean mRefreshing = false;

    protected boolean mNotify;
    protected OnRefreshListener mListener;

    protected BaseHolder mRefreshViewHolder;
    private static final int COVER_TYPE = 1;
    private static final int PULL_TYPE = 2;
    private static final int ZOOM_TYPE = 3;

    private int mRefreshType = COVER_TYPE;// 1 - 覆盖样式 2 - 下拉样式 3 - 缩放样式

    public BaseSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public BaseSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    protected Context context(){
        if (null == mContext){
            mContext = getContext();
        }
        return mContext;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
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
        if (dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            mRefreshView.setVisibility(View.GONE);
        }
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
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
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        stopNestedScroll();
    }

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

    public boolean isNestedScrollInProgress() {
        return mNestedScrollInProgress;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    protected void setHolderRefreshView(){
        if (!isMainThread()){
            return;
        }
        if (null == mRefreshViewHolder){
            Context context = context();
            if (mRefreshType == PULL_TYPE){
                mRefreshViewHolder = new PullRefreshHolder(context,mRefreshView);
            }else if (mRefreshType == ZOOM_TYPE){
                mRefreshViewHolder = new ZoomPullRefreshHolder(context,mRefreshView);
            }else {
                mRefreshViewHolder = new CoverPullRefreshHolder(context,mRefreshView);
            }
        }
        mRefreshViewHolder.setParent(this);
        mRefreshViewHolder.setRefreshView(mRefreshView);
    }

    protected abstract void finishSpinner(float overScrollTop);

    protected void moveSpinner(float overScrollTop){
        setHolderRefreshView();
        mRefreshViewHolder.moveSpinner(overScrollTop);
    }
    protected abstract boolean canChildScrollUp();

    public void doNotify(int state){
        if (mNotify) {
            if (mListener == null) { return; }
            if (state == RefreshState.START){ mListener.onRefresh(); }
        }
    }

    /**
     * @return 不能下拉刷新状态
     */
    protected boolean canNotPullToRefresh() {
        return !isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || isNestedScrollInProgress() || mRefreshView == null;
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

    public boolean isRefreshing() {
        return mRefreshing;
    }

    public void setRefreshType(int mRefreshType) { this.mRefreshType = mRefreshType; }

    public void setOnRefreshListener(OnRefreshListener listener) { mListener = listener; }
}
