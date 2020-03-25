package com.pds.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.ui.R;

/**
 * @author: pengdaosong CreateTime:  2019/2/21 5:56 PM Email：pengdaosong@medlinker.com Description:
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressView";

    private int mProgressColor;
    private int mDurationColor;
    private double mProgress;

    private Paint mPaint;

    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context,
                              @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mDurationColor = context.getResources().getColor(R.color.colorPrimaryDark);
        mProgressColor = Color.WHITE;
    }

    public CircleProgressView setProgressColor(int color){
        mProgressColor = color;
        return this;
    }

    public CircleProgressView setDurationColor(int color){
        mDurationColor = color;
        return this;
    }

    public void updateProgress(double progress){
        mProgress = progress;
        invalidate();
    }

    private RectF mRectF = new RectF();
    private static final float MAX_ANGLE = 360;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress <= 0 || mProgress >= 1){
            return;
        }
        mPaint.setColor(mDurationColor);
        int radius = getWidth() / 2;
        canvas.drawCircle(radius,radius,radius,mPaint);

        mPaint.setColor(mProgressColor);

        double angle =  MAX_ANGLE * mProgress;
        if (angle >= MAX_ANGLE){
            angle = MAX_ANGLE;
        }
        Log.i(TAG,"progress:angle = " + angle);
        mRectF.set(0,0,getWidth(),getWidth());
        canvas.drawArc(mRectF,0, (float) angle,true,mPaint);

    }

}
