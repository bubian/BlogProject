package com.pds.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 去掉默认内边距
 *
 * @author hmy
 */
public class NoPaddingTextView extends AppCompatTextView {

    private boolean mRemoveDefaultPadding = true;

    private Rect mBounds = new Rect();
    private Paint mPaint;

    {
        mPaint = getPaint(); //获取textview的画笔 获取画笔属性
    }

    public NoPaddingTextView(Context context) {
        super(context);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRemoveDefaultPadding) {
            calculateTextParams();
            //设置view的宽高为text的宽高
            setMeasuredDimension(mBounds.right - mBounds.left,
                    -mBounds.top + mBounds.bottom);
        }
    }

    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRemoveDefaultPadding) {
            drawText(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    //用drawText方法画text
    private void drawText(Canvas canvas) {
        final String text = calculateTextParams();
        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left, -mBounds.top);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        canvas.drawText(text, -left, mBounds.bottom - bottom, mPaint);
    }

    public void setRemoveDefaultPadding(boolean remove) {
        mRemoveDefaultPadding = remove;
        invalidate();
    }
}
