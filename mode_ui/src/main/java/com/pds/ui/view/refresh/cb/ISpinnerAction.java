package com.pds.ui.view.refresh.cb;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:39
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public interface ISpinnerAction {
    void finishSpinner(float overScrollTop,float slingshotDist,float totalDragDistance);
    void moveSpinner(float overScrollTop,float slingshotDist,float totalDragDistance);
    void setRefreshState(boolean isRefreshing);
    void reset();
}
