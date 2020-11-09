package com.pds.ui.nav.flextab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pds.ui.R;

import java.util.ArrayList;
import java.util.List;

public class HFlexTabLayout extends HorizontalScrollView {

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private int mTabViewLayoutId = R.layout.flex_tab_strip;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private LinearLayout mFlexTabStrip;
    private List<View> mFlexTitleViews;
    private List<View> mFlexExtraViews;
    private List<View> mFlexTabViews;

    private OnTabListener mInternalTabListener;
    private FlexTabCallback mFlexTabCallback;

    private int mSelectPosition;

    public HFlexTabLayout(Context context) {
        this(context, null);
    }

    public HFlexTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HFlexTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        addView(mFlexTabStrip, params);
    }

    private void init() {
        mFlexTitleViews = new ArrayList<>(6);
        mFlexExtraViews = new ArrayList<>(6);
        mFlexTabViews = new ArrayList<>(6);
    }

    public HFlexTabLayout setFlexTabStrip(LinearLayout tabStrip) {
        mFlexTabStrip = tabStrip;
        if (mFlexTabStrip instanceof OnTabListener) {
            setOnTabListener((OnTabListener) mFlexTabStrip);
        }
        return this;
    }

    public void toggleSelect(int position) {
        if (mSelectPosition == position) return;
        mSelectPosition = position;
        setCurrentIndex(mSelectPosition);
        if (mFlexTitleViews == null) {
            for (int i = 0, size = mFlexTabStrip.getChildCount(); i < size; i++) {
                View view = mFlexTabStrip.getChildAt(i);
                if (view instanceof IFlexTab) {
                    ((IFlexTab) view).toggleSelect(i);
                }
            }
        } else {
            for (int i = 0, size = mFlexTitleViews.size(); i < size; i++) {
                View view = mFlexTitleViews.get(i);
                if (view instanceof IFlexTab) {
                    ((IFlexTab) view).toggleSelect(i);
                }
            }
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    public void setViewPager(ViewPager viewPager) {
        mFlexTabStrip.removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    public void setViewPagerWithSingleListener(ViewPager viewPager) {
        mFlexTabStrip.removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        mFlexTitleViews.clear();
        mFlexExtraViews.clear();
        mFlexTabViews.clear();
        if (mTabViewLayoutId <= 0) {
            mTabViewLayoutId = R.layout.flex_tab_strip;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View flexTabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mFlexTabStrip, false);
            mFlexTabViews.add(flexTabView);

            View flexTabTitleView = flexTabView.findViewById(R.id.flex_tab_title);
            mFlexTitleViews.add(flexTabTitleView);

            Object data;
            if (adapter instanceof IFlexData) {
                data = ((IFlexData) adapter).tabData(i);
            } else {
                data = adapter.getPageTitle(i);
            }

            if (flexTabTitleView instanceof IFlexTab) {
                ((IFlexTab) flexTabTitleView).initView(data);
            }

            flexTabTitleView.setOnClickListener(v -> {
                for (int i1 = 0; i1 < mFlexTabStrip.getChildCount(); i1++) {
                    if (v == mFlexTabStrip.getChildAt(i1)) {
                        if (mSelectPosition == i1 && mFlexTabCallback != null) {
                            mFlexTabCallback.refresh(mSelectPosition);
                        }
                        mViewPager.setCurrentItem(i1);
                        mSelectPosition = i1;
                        setCurrentIndex(mSelectPosition);
                        return;
                    }
                }
            });
            mFlexTabStrip.addView(flexTabTitleView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mFlexTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mFlexTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                targetScrollX -= mTitleOffset;
            }
            scrollTo(targetScrollX, 0);
            if (mInternalTabListener != null) {
                mInternalTabListener.onScroll(tabIndex, positionOffset, targetScrollX);
            }
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mFlexTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }
            doViewPagerPageChanged(position, positionOffset);
            View selectedTitle = mFlexTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                doViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
            if (mSelectPosition != position)
                mSelectPosition = position;
                setCurrentIndex(mSelectPosition);
        }
    }

    private void setCurrentIndex(int index) {
        if (mFlexTabStrip instanceof IFlexTab) {
            ((IFlexTab) mFlexTabStrip).setCurrentSelectIndex(index);
        }
    }

    private void doViewPagerPageChanged(int position, float positionOffset) {
        if (mFlexTabStrip instanceof IFlexTab) {
            ((IFlexTab) mFlexTabStrip).onViewPagerPageChanged(position, positionOffset);
        }
    }

    private void setOnTabListener(OnTabListener l) {
        mInternalTabListener = l;
    }

    public interface OnTabListener {
        void onScroll(int tabIndex, int positionOffset, int targetScrollX);
    }
}
