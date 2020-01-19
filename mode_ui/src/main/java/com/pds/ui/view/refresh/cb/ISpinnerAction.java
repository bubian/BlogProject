package com.pds.ui.view.refresh.cb;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:39
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public interface ISpinnerAction {
    void finishSpinner(float overScrollTop);
    void moveSpinner(float overScrollTop);
    void reset();
}
