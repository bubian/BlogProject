package com.pds.ui.view.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.pds.ui.R;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/24 4:29 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class BgLinearLayout extends LinearLayout {

    private int mRadius;
    private int mStrokeWidth;
    private int mStrokeColor;
    private int mBgColor;

    private RectF mRectF = new RectF();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mIsDrawStroke = false;
    private boolean mIsDrawRadius = false;

    public BgLinearLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BgLinearLayout(Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BgLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BgLinearLayout);
        mRadius = a.getDimensionPixelSize(R.styleable.BgLinearLayout_cbRadius, 0);
        mStrokeWidth = a
                .getDimensionPixelSize(R.styleable.BgLinearLayout_cbStrokeWidth, defStyleAttr);
        mStrokeColor = a.getColor(R.styleable.BgLinearLayout_cbStrokeColor, Color.TRANSPARENT);
        mBgColor = a.getColor(R.styleable.BgLinearLayout_cbBgColor, Color.TRANSPARENT);
        a.recycle();

        mIsDrawStroke = mStrokeWidth > 0 && mStrokeColor != Color.TRANSPARENT;
        mIsDrawRadius = mRadius > 0 && mBgColor != Color.TRANSPARENT;
        setWillNotDraw(false);
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        mRectF.set(2.5f, 2.5f, getWidth() - 2.5f, getHeight() - 2.5f);
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
        super.draw(canvas);
    }
}
