/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pds.ui.view.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.pds.ui.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    private Runnable mTabSelector;
    private long mLastClickTime;
    private long mLastDoubleTime;
    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
            if (oldSelected == newSelected && mTabReselectedListener != null) {
                mTabReselectedListener.onTabReselected(newSelected);
            }
            //双击事件
            final long time = System.currentTimeMillis();
            final long delta = time - mLastClickTime;
            if (delta <= ViewConfiguration.getDoubleTapTimeout() && delta >= 40 && time - mLastDoubleTime > 2000) {//ViewConfiguration.getDoubleTapMinTime() = 40
                tabView.setTag(newSelected);
                if (mDoubleClickListener != null) {
                    mDoubleClickListener.onClick(tabView);
                }
                mLastDoubleTime = time;
            }
            mLastClickTime = time;
        }
    };

    private final IcsLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private SparseIntArray mChildWidths = new SparseIntArray();
    private int mMaxTabWidth;
    private int mMaxTextWidth;
    private boolean mMatchParent = false; // 子控件的宽度是否能够填充满父控件
    private int mSelectedTabIndex;

    private OnTabReselectedListener mTabReselectedListener;
    private OnClickListener mDoubleClickListener;
    private int customTabsBackgroundRes;

    private static final int TITLE_OFFSET_DIPS = 24;
    private int mTitleOffset;
    private int mScrollState;
    private int mRedOffset;

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new IcsLinearLayout(context);
        mTabLayout.setGravity(Gravity.CENTER);
        mTabLayout.setmDividerWidth(dip2px(getContext(), 16));
        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setDraHorizontalIndicator(boolean isShow) {
        mTabLayout.setDraHorizontalIndicator(isShow);
    }

    public void setTabDividerWidth(int px) {
        mTabLayout.setmDividerWidth(px);
    }

    public void setTabDividerHeight(int px) {
        mTabLayout.setSelectedIndicatorThickness(px);
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    public void setOnTabDoubleClickListener(OnClickListener clickListener) {
        mDoubleClickListener = clickListener;
    }

    public void setCustomTabsBackgroundRes(int backgroundRes) {
        this.customTabsBackgroundRes = backgroundRes;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            float width = MeasureSpec.getSize(widthMeasureSpec);
            if (childCount > 2) {
                int size = mChildWidths.size();
                int totalWidth = 0;
                for (int i = 0; i < size; i++) {
                    totalWidth += mChildWidths.get(i);
                }
                if (totalWidth >= width) {
                    mMatchParent = true;
                } else {
                    mMatchParent = false;
                    if (mMaxTextWidth * childCount < width) {
                        mMaxTabWidth = (int) (width / childCount);
                    }
                }
            } else {
                mMaxTabWidth = (int) (width / 2);
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private void addTab(int index, CharSequence text, int iconResId) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        final TextView tv = tabView.getTextView();
        tv.setText(text);

        if (iconResId != 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        }
        int width = (int) Layout.getDesiredWidth(text, tv.getPaint());
        width = width + tabView.getPaddingLeft() + tabView.getPaddingRight() + tv.getPaddingLeft() + tv.getPaddingRight();
        mChildWidths.put(index, width);
        mMaxTextWidth = Math.max(width, mMaxTextWidth);
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, dip2px(getContext(), 40), 1));
    }

    public void setTabPadding(int left, int right) {
        mTabLayout.setPadding(left, 0, right, 0);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        mScrollState = arg0;
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        mTabLayout.onPageScroll(arg0, arg1);
        View selectedTitle = mTabLayout.getChildAt(arg0);
        int extraOffset = (selectedTitle != null)
                ? (int) (arg1 * selectedTitle.getWidth())
                : 0;
        scrollToTab(arg0, extraOffset);
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        selectedTabIndex(arg0);
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mTabLayout.onPageScroll(arg0, 0f);
            setCurrentItem(arg0);
        }
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    private void selectedTabIndex(int selectedTabIndex) {
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == selectedTabIndex);
            child.setSelected(isSelected);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mMaxTextWidth = 0;
        mTabLayout.removeAllViews();
        mChildWidths.clear();
        mMatchParent = false;
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);

    /*    final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }*/
        selectedTabIndex(item);
//        scrollToTab(item, 0);
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabLayout.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }
        View selectedChild = mTabLayout.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }
            scrollTo(targetScrollX, 0);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    public class TabView extends FrameLayout {

        private int mIndex;
        private TextView mTextView;
        private View mTvRedPoint;

        public TabView(Context context) {
            super(context, null, R.attr.vpiTabPageIndicatorStyle);
            mTextView = new TextView(context, null, R.attr.vpiTabPageIndicatorTabsStyle);
            mTextView.setSingleLine();
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            addView(mTextView, params);

            mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    final int width = dip2px(getContext(), 5);
                    int maginRight = 0;

                    final int rightDistance = (getWidth() - mTextView.getRight());
                    if (rightDistance - getPaddingRight() - 2 > width) {
                        maginRight = rightDistance - getPaddingRight();
                    }

                    mTvRedPoint = new View(getContext());
                    mTvRedPoint.setBackgroundResource(R.drawable.redpoint);
                    LayoutParams paramsRedPoint = new LayoutParams(width, width);
                    paramsRedPoint.gravity = Gravity.RIGHT;
                    paramsRedPoint.setMargins(0, mTextView.getTop() + mTextView.getPaddingTop() - mRedOffset, maginRight - mRedOffset, 0);
                    mTvRedPoint.setVisibility(INVISIBLE);
                    addView(mTvRedPoint, paramsRedPoint);
                    if (Build.VERSION.SDK_INT < 16) {
                        mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = 0;
            if (mMatchParent) {
                width = mChildWidths.get(mIndex);
            } else {
                width = Math.max(mMaxTabWidth, mMaxTextWidth);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
        }


        public int getIndex() {
            return mIndex;
        }

        public TextView getTextView() {
            return mTextView;
        }

        public View getRedView() {
            return mTvRedPoint;
        }
    }

    public void setRedOffset(int redOffset) {
        this.mRedOffset = redOffset;
    }
    /**
     * 得到指定位置上面的tabview
     *
     * @param position
     * @return
     */
    public TabView getTabViewAtPositon(int position) {
        return (TabView) mTabLayout.getChildAt(position);
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        int count = mTabLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            mTabLayout.getChildAt(i).setClickable(clickable);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
