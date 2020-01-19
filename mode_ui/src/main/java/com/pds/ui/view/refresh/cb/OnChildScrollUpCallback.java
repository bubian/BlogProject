package com.pds.ui.view.refresh.cb;

import android.view.View;

import androidx.annotation.Nullable;

import com.pds.ui.view.refresh.MultipleSwipeRefreshLayout;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public interface OnChildScrollUpCallback {
    boolean canChildScrollUp(MultipleSwipeRefreshLayout parent, @Nullable View child);
}
