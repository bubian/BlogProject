package com.pds.ui.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.pds.ui.R;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/14 3:25 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ContainerDelegate {

    private int mRadius;
    private int mStrokeWidth;
    private int mStrokeColor;
    private int mBgColor;

    private RectF mRectF = new RectF();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mIsDrawStroke = false;
    private boolean mIsDrawRadius = false;

    public void init(Context context, AttributeSet attrs, int defStyleAttr, View view) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BgContainer);
        mRadius = a.getDimensionPixelSize(R.styleable.BgContainer_cbRadius, 0);
        mStrokeWidth = a
                .getDimensionPixelSize(R.styleable.BgContainer_cbStrokeWidth, defStyleAttr);
        mStrokeColor = a.getColor(R.styleable.BgContainer_cbStrokeColor, Color.TRANSPARENT);
        mBgColor = a.getColor(R.styleable.BgContainer_cbBgColor, Color.TRANSPARENT);
        a.recycle();

        mIsDrawStroke = mStrokeWidth > 0 && mStrokeColor != Color.TRANSPARENT;
        mIsDrawRadius = mRadius > 0 && mBgColor != Color.TRANSPARENT;
        view.setWillNotDraw(false);
    }

    public void setBgColor(int bgColor, View view) {
        mBgColor = bgColor;
        view.invalidate();
    }

    public void draw(Canvas canvas, View view) {
        mRectF.set(2.5f, 2.5f, view.getWidth() - 2.5f, view.getHeight() - 2.5f);
        if (mIsDrawRadius) {
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mBgColor);
            mPaint.setStyle(Style.FILL);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        }

        if (mIsDrawStroke) {
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Style.STROKE);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        }
    }
}
