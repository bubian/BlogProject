package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;

import com.pds.ui.view.refresh.cb.IRefreshTrigger;
import com.pds.ui.view.refresh.cb.ISpinnerAction;
import com.pds.ui.view.refresh.cb.ICover;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:41
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BaseHolder implements IRefreshTrigger, ISpinnerAction {

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

    @Override
    public void finishSpinner(float overScrollTop) {

    }

    @Override
    public void moveSpinner(float overScrollTop) {

    }

    @Override
    public void reset() {

    }
}
