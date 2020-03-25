package com.pds.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.ui.R;
import com.pds.util.unit.UnitConversionUtils;

/**
 * @author: pengdaosong CreateTime:  2019/3/1 1:54 PM Email：pengdaosong@medlinker.com Description:
 */
public class CircleRingProgressView extends View {

    private Context mContext;
    private static final int RING_WIDTH = 6;
    /**
     * 圆环角度数
     */
    private int mRingAngle;
    /**
     * 圆环的宽度
     */
    private final int mRingWidth;
    /**
     * 圆环的背景颜色
     */
    private final int mRingBgColor;
    /**
     * 圆环的进度颜色
     */
    private final int mRingProgressColor;
    /**
     * 进度
     */
    private float mProgress;
    /**
     * 圆环起始位置
     */
    private int mStartAngle;

    private Paint mPaint;
    private RectF mRectF = new RectF();

    public CircleRingProgressView(Context context) {
        this(context, null);
    }

    public CircleRingProgressView(Context context,
                                  @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRingProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleRingProgressView);
        mRingWidth = a.getDimensionPixelSize(R.styleable.CircleRingProgressView_crpv_ring_width,
                UnitConversionUtils.dip2px(mContext, RING_WIDTH));
        mRingProgressColor = a.getColor(R.styleable.CircleRingProgressView_crpv_progress_color,
                mContext.getResources().getColor(
                        R.color.color_f684033));
        mRingBgColor = a.getColor(
                R.styleable.CircleRingProgressView_crpv_bg_color, mContext.getResources().getColor(
                        R.color.color_fff2c1));
        mRingAngle = a.getInt(R.styleable.CircleRingProgressView_crpv_ring_angle, 320);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);

        mStartAngle = (360 - mRingAngle) / 2 - 270;
    }

    public void updateProgress(float progress) {
        if (progress >= 1) {
            progress = 1;
        }
        mProgress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cap = mRingWidth / 2;
        mRectF.set(cap, cap, getWidth() - cap, getHeight() - cap);
        drawRingBg(canvas);
        drawRingProgress(canvas);


    }

    private void drawRingBg(Canvas canvas) {
        mPaint.setColor(mRingBgColor);
        canvas.drawArc(mRectF, mStartAngle, mRingAngle, false, mPaint);
    }

    private void drawRingProgress(Canvas canvas) {
        mPaint.setColor(mRingProgressColor);
        int angle = (int) (mRingAngle * mProgress);
        if (angle >= mRingAngle) {
            angle = mRingAngle;
        }
        canvas.drawArc(mRectF, mStartAngle, angle, false, mPaint);
    }
}
