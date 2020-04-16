package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.view.View;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 15:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ZoomPullRefreshHolder extends BaseHolder{
    public ZoomPullRefreshHolder(Context context) {
        super(context);
    }

    public ZoomPullRefreshHolder(Context context, View refreshView) {
        super(context, refreshView);
    }

    @Override
    public void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void setRefreshState(boolean isRefreshing) {

    }
}
