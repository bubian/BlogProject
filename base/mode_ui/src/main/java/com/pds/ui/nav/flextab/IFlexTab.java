package com.pds.ui.nav.flextab;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/2 5:17 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public interface IFlexTab {
    void initView(Object o);
    void toggleSelect(int position);
    void onViewPagerPageChanged(int position,float positionOffset);
    void onScroll(int tabIndex, int positionOffset, int targetScrollX);
    void setCurrentSelectIndex(int index);
}
