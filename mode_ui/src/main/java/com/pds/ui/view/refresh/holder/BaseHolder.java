package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;
import com.pds.ui.view.refresh.cb.IRefreshTrigger;
import com.pds.ui.view.refresh.cb.ISpinnerAction;
import com.pds.ui.view.refresh.cb.ICover;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:41
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public abstract class  BaseHolder implements IRefreshTrigger {

    protected Context mContext;
    protected View mRefreshView;
    protected int mTouchSlop;

    public BaseHolder(Context context){
        this(context,null);
    }

    public BaseHolder(Context context,View refreshView){
        mContext = context;
        mRefreshView = refreshView;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setRefreshView(View refreshView){
        mRefreshView = refreshView;
    }

    @Override
    public void onPullDownState(float progress) {

    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onReleaseToRefresh() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void init() {

    }

    public void finishSpinner(float overScrollTop) {

    }

    public void moveSpinner(float overScrollTop) {

    }

    public void reset() {

    }

    public void setParent(BaseSwipeRefreshLayout mParent){

    }

    public void measureChildAfter(ViewGroup parent, int widthMeasureSpec, int heightMeasureSpec){

    }

    public abstract void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec);

    public void layoutChild(boolean changed, int left, int top, int right, int bottom){

    }

    public boolean onInterceptTouchEvent(MotionEvent ev){
        return false;
    }

    public void setRefreshState(boolean mRefreshing){

    }

    public void setRefreshing(boolean refreshing){

    }

    public void setRefreshing(boolean refreshing,boolean notify){

    }

    public boolean isRefreshing(float overScrollTop){
        return false;
    }
}
