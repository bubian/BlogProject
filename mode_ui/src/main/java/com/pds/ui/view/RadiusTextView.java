package com.pds.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.pds.ui.R;

/**
 * <p>
 * CreateTime:  2018/12/4 3:43 PM
 * <p>
 * Email：pengdaosong@medlinker.com.
 * <p>
 * Description:
 * @author pengdaosong
 */
public class RadiusTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mRadius;
    private int mStrokeWidth;
    private int mStrokeColor;
    private int mBgColor;
    private boolean mIsDrawStroke = false;
    private boolean mIsDrawRadius = false;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();

    public RadiusTextView(Context context) {
        this(context, null);
    }

    public RadiusTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadiusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadiusTextView);
        mRadius = a.getDimensionPixelSize(R.styleable.RadiusTextView_rtvRadius, 0);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.RadiusTextView_rtvStrokeWidth, 0);
        mStrokeColor = a.getColor(R.styleable.RadiusTextView_rtvStrokeColor, Color.TRANSPARENT);
        a.recycle();
        mIsDrawStroke = mStrokeWidth > 0 && mStrokeColor != Color.TRANSPARENT;
        mIsDrawRadius = mRadius > 0;
        mBgColor = getBgColor();
    }

    private int getBgColor() {
        Drawable drawable = getBackground();
        if (null == drawable) {
            return Color.TRANSPARENT;
        }
        int color = ((ColorDrawable) drawable).getColor();
        setBackgroundColor(Color.TRANSPARENT);
        return color;
    }

    public void setBgColor(int color) {
        mBgColor = color;
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setAttributes(boolean hasStroke,int backgroundColor,int textColor) {
        mBgColor = backgroundColor;
        mIsDrawStroke = hasStroke;
        setTextColor(textColor);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setAttributes(boolean hasStroke,int textColor) {
        mIsDrawStroke = hasStroke;
        setTextColor(textColor);
        setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    protected void onDraw(Canvas canvas) {
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
        super.onDraw(canvas);

    }
}
