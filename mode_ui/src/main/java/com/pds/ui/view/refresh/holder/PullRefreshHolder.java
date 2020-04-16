package com.pds.ui.view.refresh.holder;

import android.content.Context;
import android.view.View;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:46
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PullRefreshHolder extends BaseHolder {

    public PullRefreshHolder(Context context, View refreshView) {
        super(context,refreshView);
    }

    @Override
    public void measureChildAfter(BaseSwipeRefreshLayout parent, int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void setRefreshState(boolean isRefreshing) {

    }
}

