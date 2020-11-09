package com.pds.ui.nav.slidingtab;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/3 2:51 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class RoundSlidingTabStrip extends SlidingTabStrip{

    public RoundSlidingTabStrip(Context context) {
        this(context,null);
    }

    public RoundSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void drawIndicator(int left, int height, int right, int color, Canvas canvas) {
        mRectF.set(left, 0, right, height);
        mSelectedIndicatorPaint.setAntiAlias(true);
        int r = height / 2;
        canvas.drawRoundRect(mRectF, r, r, mSelectedIndicatorPaint);
    }
}
