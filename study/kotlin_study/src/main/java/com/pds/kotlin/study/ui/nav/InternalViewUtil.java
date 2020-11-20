package com.pds.kotlin.study.ui.nav;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import com.pds.kotlin.study.R;
import com.pds.ui.ModuleUI;
import com.pds.ui.nav.slidingtab.SlidingTabLayout;
import com.pds.util.unit.UnitConversionUtils;


/**
 * Created by heaven7 on 2016/9/5.
 */
public class InternalViewUtil {

    public static void initSlidingTablayout(final SlidingTabLayout mSlidingTabLayout, final ViewPager vp, boolean splitEqual) {
        initSlidingTablayout(mSlidingTabLayout, vp, splitEqual, null);
    }

    public static void initSlidingTablayout(final SlidingTabLayout mSlidingTabLayout, final ViewPager vp, boolean splitEqual,
                                            final ViewPager.OnPageChangeListener opl) {
        final Context context = mSlidingTabLayout.getContext();
        mSlidingTabLayout.setDrawBottomUnderLine(false);
        mSlidingTabLayout.setBottomIndicatorHeight(1);
        mSlidingTabLayout.setSelectIndicatorHeight(UnitConversionUtils.dip2px(ModuleUI.instance().appContext(),2));
        if (splitEqual) {
            mSlidingTabLayout.setCustomTabView(R.layout.item_equal_tab, R.id.tv_title);
        } else {
            mSlidingTabLayout.setCustomTabView(R.layout.item_home_tab_title, R.id.tv_title);
        }
        mSlidingTabLayout.setDrawVerticalIndicator(false);
        mSlidingTabLayout.setDrawHorizontalIndicator(true);
        mSlidingTabLayout.setSelectRelativeTextColorsRes(R.color.c_0064ff, R.color.c_8c8c93);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.AbsTabColorizer(context) {
            @Override
            protected int getIndicatorColorRes(int position) {
                return R.color.c_0064ff;
            }

            @Override
            protected int getDividerColorRes(int position) {
                return R.color.c_0064ff;
            }
        });
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSlidingTabLayout.toogleSelect(vp.getCurrentItem());
                if (opl != null) {
                    opl.onPageSelected(position);
                }
            }
        });
    }
}
