package com.pds.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.pds.ui.R;

import java.util.ArrayList;

/**
 * @author pengdaosong
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class MarqueeTextView extends AppCompatTextView {

    private static final int DEFAULT_VERTICAL_SPEED = 500;
    private static final int DEFAULT_VERTICAL_INTERVAL = 1000;
    private static final int DEFAULT_HORIZONTAL_SPEED = 500;
    private static final int DEFAULT_HORIZONTAL_LOOP_SPEED = 1000;

    private ArrayList<String> contentList = new ArrayList<>();
    private String singleText = "";

    private int verticalSwitchSpeed;
    private int verticalSwitchInterval;
    private int horizontalScrollSpeed;
    private int horizontalLoopSpeed;

    private int viewHeight;
    private int viewWidth;

    private int contentColor = Color.WHITE;
    private int contentTextSize = 100;
    private boolean hasInited = false;
    /**
     * 垂直方向滚动是否已经初始化
     */
    private boolean hasVerticalInited = false;

    private int currentY = 0;
    private int currentX = 0;
    private int xOffset = 0;
    private int yStartPos = 0;
    private int xStartPos = 0;
    private int currentIndex = 0;
    private Paint contentPaint;
    private int maxContentWidth = 0;
    private int maxContentHeight = 0;
    private boolean isHorizontalRunning = false;
    private boolean isVerticalRunning = false;
    private boolean horizontalOriLeft = true;

    public MarqueeTextView(Context context) {
        super(context);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        verticalSwitchSpeed = array.getInt(R.styleable.MarqueeTextView_vertical_switch_speed, DEFAULT_VERTICAL_SPEED);
        verticalSwitchInterval = array.getInt(R.styleable.MarqueeTextView_vertical_switch_interval, DEFAULT_VERTICAL_INTERVAL);
        horizontalScrollSpeed = array.getInt(R.styleable.MarqueeTextView_horizontal_scroll_speed, DEFAULT_HORIZONTAL_SPEED);
        horizontalLoopSpeed = array.getInt(R.styleable.MarqueeTextView_horizontal_loop_speed, DEFAULT_HORIZONTAL_LOOP_SPEED);
        contentColor = array.getColor(R.styleable.MarqueeTextView_content_text_color, Color.BLACK);
        contentTextSize =array.getDimensionPixelSize(R.styleable.MarqueeTextView_content_text_size, 15);
        singleText = array.getString(R.styleable.MarqueeTextView_content_single_text);
        array.recycle();
    }

    private void init() {
        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
        contentPaint.setDither(true);
        contentPaint.setTextSize(contentTextSize);
        contentPaint.setColor(contentColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (null != contentList && contentList.size() > 0) {
            for (int i = 0; i < contentList.size(); i++) {
                String contentString = contentList.get(i);
                Rect contentBound = new Rect();
                contentPaint.getTextBounds(contentString, 0, contentString.length(), contentBound);
                int tempWidth = contentBound.width();
                int tempHeight = contentBound.height();
                maxContentHeight = Math.max(maxContentHeight, tempHeight);
                maxContentWidth = Math.max(maxContentWidth, tempWidth);
            }
        } else if (!TextUtils.isEmpty(singleText)) {
            Rect contentBound = new Rect();
            contentPaint.getTextBounds(singleText, 0, singleText.length(), contentBound);
            maxContentWidth = contentBound.width();
            maxContentHeight = contentBound.height();
        }
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, maxContentHeight);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(maxContentWidth, heightSize);
        } else {
            setMeasuredDimension(maxContentWidth, maxContentHeight);
        }
    }

    private Rect contentBound = new Rect();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != contentList && contentList.size() > 1) {
            doMultiLineText(canvas);
        } else if (!TextUtils.isEmpty(singleText)) {
            doSingleLineText(canvas);
        }
    }

    private void doSingleLineText(Canvas canvas){
        viewHeight = getMeasuredHeight() + 10;
        viewWidth = getMeasuredWidth();
        contentBound.set(0,0,0,0);
        contentPaint.getTextBounds(singleText, 0, singleText.length(), contentBound);
        xOffset = contentBound.width() - viewWidth;

        Paint.FontMetrics fontMetrics = contentPaint.getFontMetrics();
        int textHeight = (int) ((-fontMetrics.ascent - fontMetrics.descent) / 2);
        yStartPos = viewHeight / 2 + maxContentHeight / 4 + textHeight / 4;

        if (!hasInited) {
            hasInited = true;
            currentX = 0;
            xStartPos = currentX;
        }
        if (xOffset > 0) {
            xOffset += contentTextSize * 2;
            if (!isHorizontalRunning) {
                isHorizontalRunning = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startHorizontalLoop();
                }
            }
        }
        canvas.drawText(singleText, currentX, yStartPos, contentPaint);
    }

    private void doMultiLineText(Canvas canvas){
        if (currentIndex >= contentList.size()) {
            currentIndex = 0;
        }
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();

        String currentString = contentList.get(currentIndex);
        int nextIndex = currentIndex + 1;
        if (currentIndex + 1 >= contentList.size()) {
            nextIndex = 0;
        }
        String nextString = contentList.get(nextIndex);

        contentBound.set(0,0,0,0);
        contentPaint.getTextBounds(currentString, 0, currentString.length(), contentBound);
        xOffset = contentBound.width() - viewWidth;

        Paint.FontMetrics fontMetrics = contentPaint.getFontMetrics();
        int textHeight = (int) ((-fontMetrics.ascent - fontMetrics.descent) / 2);
        yStartPos = viewHeight / 2 + maxContentHeight / 4 + textHeight / 4;

        if (!hasVerticalInited) {
            hasVerticalInited = true;
            currentY = yStartPos;
        }

        if (xOffset > 0) {
            //另外加点留白.设留白两个字宽
            xOffset += contentTextSize * 2;
            if (!isHorizontalRunning && !isVerticalRunning) {
                isHorizontalRunning = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startHorizontalScroll();
                }
                currentX = 0;
            }
        } else {
            if (!isVerticalRunning) {
                isVerticalRunning = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startVerticalInterval();
                }
                currentX = 0;
            }
        }
        canvas.drawText(currentString, currentX, currentY, contentPaint);
        canvas.drawText(nextString, 0, currentY + viewHeight, contentPaint);
    }

    public void setContentList(final ArrayList<String> list, final String text, String marqueeTextViewType) {
        contentList.clear();
        if (list.size() > 1) {
            contentList.addAll(list);
        } else if (list.size() == 1) {
            singleText = list.get(0);
        } else {
            singleText = text;
        }
        hasInited = false;
        //解决垂直方向一开始滚动卡顿问题
        hasVerticalInited = false;
        requestLayout();
        postInvalidate();
    }

    private LinearInterpolator  mLinearInterpolator = new LinearInterpolator();

    private void startVerticalInterval() {
        ValueAnimator verticalIntervalAnimator = ValueAnimator.ofFloat(0, 1);
        verticalIntervalAnimator.setDuration(verticalSwitchInterval);
        verticalIntervalAnimator.setInterpolator(mLinearInterpolator);
        verticalIntervalAnimator.start();
        verticalIntervalAnimator.addListener(mVerticalIntervalListenerAdapter);
    }

    private AnimatorListenerAdapter mVerticalIntervalListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            startVerticalSwitch();
        }
    };

    private void startVerticalSwitch() {
        ValueAnimator verticalSwitchAnimator = ValueAnimator.ofFloat(0, 1);
        verticalSwitchAnimator.setDuration(verticalSwitchSpeed);
        verticalSwitchAnimator.start();
        verticalSwitchAnimator.addUpdateListener(mVerticalSwitchUpdateListener);
        verticalSwitchAnimator.addListener(mVerticalSwitchListenerAdapter);
    }

    private AnimatorListenerAdapter mVerticalSwitchListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            currentIndex++;
            currentY = yStartPos;
            isVerticalRunning = false;
            postInvalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mVerticalSwitchUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            currentY = (int) (yStartPos - value * viewHeight * 1);
            postInvalidate();
        }
    };

    private void startHorizontalScroll() {
        ValueAnimator horizontalScrollAnimator = ValueAnimator.ofFloat(0, 1);
        if (horizontalScrollSpeed * xOffset / contentTextSize < 0) {
            isHorizontalRunning = false;
            return;
        }
        horizontalScrollAnimator.setDuration(horizontalScrollSpeed * xOffset / contentTextSize);
        horizontalScrollAnimator.setInterpolator(mLinearInterpolator);
        horizontalScrollAnimator.start();
        horizontalScrollAnimator.addUpdateListener(mHorizontalScrollUpdateListener);
        horizontalScrollAnimator.addListener(mHorizontalScrollListenerAdapter);
    }

    private AnimatorListenerAdapter mHorizontalScrollListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            isHorizontalRunning = false;
            isVerticalRunning = true;
            startVerticalInterval();
            postInvalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mHorizontalScrollUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            currentX = (int) (-xOffset * value);
            postInvalidate();
        }
    };

    private void startHorizontalLoop() {
        ValueAnimator horizontalScrollAnimator;
        if (horizontalOriLeft) {
            horizontalScrollAnimator = ValueAnimator.ofFloat(0, 1);
        } else {
            horizontalScrollAnimator = ValueAnimator.ofFloat(0, -1);
        }
        if (horizontalScrollSpeed * xOffset / contentTextSize < 0) {
            isHorizontalRunning = false;
            return;
        }
        horizontalScrollAnimator.setDuration(horizontalLoopSpeed * xOffset / contentTextSize);
        horizontalScrollAnimator.setInterpolator(mLinearInterpolator);
        horizontalScrollAnimator.start();
        horizontalScrollAnimator.addUpdateListener(mHorizontalLoopUpdateListener);
        horizontalScrollAnimator.addListener(mHorizontalLoopListenerAdapter);
    }


    private AnimatorListenerAdapter mHorizontalLoopListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            isHorizontalRunning = false;
            horizontalOriLeft = !horizontalOriLeft;
            xStartPos = currentX;
            postInvalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mHorizontalLoopUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            currentX = (int) (xStartPos - xOffset * value);
            postInvalidate();
        }
    };

    public void setSingleText(String singleText) {
        this.singleText = singleText;
        postInvalidate();
    }
}