package com.pds.ui.nav.flextab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;


class DefaultFlexTabStrip extends LinearLayout implements IFlexTab {

    private static final int DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2;
    private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;
    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 8;
    private static final int DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF33B5E5;

    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final byte DEFAULT_DIVIDER_COLOR_ALPHA = 0x20;
    private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;

    int mBottomBorderThickness;
    private final Paint mBottomBorderPaint;

    int mSelectedIndicatorThickness;
    private final Paint mSelectedIndicatorPaint;

    private final int mDefaultBottomBorderColor;

    private final Paint mDividerPaint;
    private final float mDividerHeight;

    private int mSelectedPosition;
    private float mSelectionOffset;

    private boolean mDrawHorizontalIndicator = true;
    private boolean mDrawVerticalIndicator;
    boolean mDrawBottomUnderline;

    float mBottomBorderIndicatorRatio = 1.0f;
    private RectF mRectF = new RectF();
    private boolean mIsShowRound = true;

    private int mSelectPosition;
    private int[] mIndicatorColors;
    private int[] mDividerColors;

    public DefaultFlexTabStrip(Context context) {
        this(context, null);
    }

    public DefaultFlexTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        final float density = getResources().getDisplayMetrics().density;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data;

        mDefaultBottomBorderColor = setColorAlpha(themeForegroundColor, DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);
        setDividerColors(setColorAlpha(themeForegroundColor, DEFAULT_DIVIDER_COLOR_ALPHA));

        mBottomBorderThickness = (int) (DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        mBottomBorderPaint = new Paint();
        mBottomBorderPaint.setColor(mDefaultBottomBorderColor);

        mSelectedIndicatorThickness = (int) (SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();

        mDividerHeight = DEFAULT_DIVIDER_HEIGHT;
        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth((int) (DEFAULT_DIVIDER_THICKNESS_DIPS * density));

    }

    void setDrawHorizontalIndicator(boolean drawHorizontalIndicator) {
        this.mDrawHorizontalIndicator = drawHorizontalIndicator;
    }

    void setDrawVerticalIndicator(boolean drawVerticalIndicator) {
        this.mDrawVerticalIndicator = drawVerticalIndicator;
    }

    void setIndicatorColors(int... colors) {
        mIndicatorColors = colors;
    }

    int getIndicatorColors(int position) {
        if(null != mIndicatorColors && position < mIndicatorColors.length){
            return mIndicatorColors[position];
        }
        return Color.TRANSPARENT;
    }

    void setDividerColors(int... colors) {
        mDividerColors = colors;
    }

    int getDividerColor(int position) {
        if(null != mDividerColors && position < mDividerColors.length){
            return mDividerColors[position];
        }
        return Color.TRANSPARENT;
    }

    @Override
    public void initView(Object o) {

    }

    @Override
    public void toggleSelect(int position) {

    }

    @Override
    public void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();
        float ratio = 1 - mBottomBorderIndicatorRatio;
        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int titleWidth = selectedTitle.getWidth();
            int left = (int) (selectedTitle.getLeft() + ratio * titleWidth / 2);
            int right = (int) (left + mBottomBorderIndicatorRatio * titleWidth);

            int color = getIndicatorColors(mSelectedPosition);
            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                int nextColor = getIndicatorColors(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }
                View nextTitle = getChildAt(mSelectedPosition + 1);
                int newTitleLeft = (int) (nextTitle.getLeft() + ratio * nextTitle.getWidth() / 2);
                int newTitleRight = (int) (newTitleLeft + mBottomBorderIndicatorRatio * nextTitle.getWidth());

                left = (int) (mSelectionOffset * newTitleLeft + (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * newTitleRight + (1.0f - mSelectionOffset) * right);
            }
            if (mDrawHorizontalIndicator) {
                mSelectedIndicatorPaint.setColor(color);
                mRectF.set(left, height - mSelectedIndicatorThickness, right, height);
                if (mIsShowRound) {
                    canvas.drawRoundRect(mRectF, 10, 10, mSelectedIndicatorPaint);
                } else {
                    canvas.drawRect(mRectF, mSelectedIndicatorPaint);
                }
                if (mDrawBottomUnderline) {
                    canvas.drawRect(0, height - mBottomBorderThickness, getWidth(), height, mBottomBorderPaint);
                }
            }
        }
        if (mDrawVerticalIndicator) {
            final int dividerHeightPx = (int) (Math.min(Math.max(0f, mDividerHeight), 1f) * height);
            int separatorTop = (height - dividerHeightPx) / 2;
            for (int i = 0; i < childCount - 1; i++) {
                View child = getChildAt(i);
                mDividerPaint.setColor(getDividerColor(i));
                canvas.drawLine(child.getRight(), separatorTop, child.getRight(),
                        separatorTop + dividerHeightPx, mDividerPaint);
            }
        }
    }

    public void setIsShowRound(boolean isShowRound) {
        mIsShowRound = isShowRound;
    }

    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onScroll(int tabIndex, int positionOffset, int targetScrollX) {
        //mRoundLeftX =  getChildAt(tabIndex).getLeft() + positionOffset + mPaddingLeftRight;
    }

    @Override
    public void setCurrentSelectIndex(int index) {
        mSelectPosition = index;
    }
}
