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
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.pds.ui.R;
import com.pds.util.UnitConversionUtils;

import java.util.ArrayList;

/**
 * @author shenjj
 * @date 2016/12/26
 */

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
    //垂直方向滚动是否已经初始化
    private boolean hasVerticalInited = false;

    private int currentY = 0;
    private int currnetX = 0;
    private int xOffset = 0;
    private int yStartPos = 0;
    private int xStartPos = 0;
    private int currnetIndex = 0;
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
        contentTextSize = (int) array.getDimension(R.styleable.MarqueeTextView_content_text_size, UnitConversionUtils.sp2Px(getContext(),15));
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int contentWidth;
        if (null != contentList && contentList.size() > 1) {
            if (currnetIndex >= contentList.size()) {
                currnetIndex = 0;
            }
            viewHeight = getMeasuredHeight();
            viewWidth = getMeasuredWidth();

            String currentString = contentList.get(currnetIndex);
            int nextIndex = currnetIndex + 1;
            if (currnetIndex + 1 >= contentList.size()) {
                nextIndex = 0;
            }
            String nextString = contentList.get(nextIndex);

            Rect contentBound = new Rect();
            contentPaint.getTextBounds(currentString, 0, currentString.length(), contentBound);
            contentWidth = contentBound.width();
            xOffset = contentWidth - viewWidth;

            Paint.FontMetrics fontMetrics = contentPaint.getFontMetrics();
            int textHeight = (int) ((-fontMetrics.ascent - fontMetrics.descent) / 2);
            yStartPos = viewHeight / 2 + maxContentHeight / 4 + textHeight / 4;

            //delete by kexi
//            if (!hasInited) {
//                hasInited = true;
//                currentY = yStartPos;
//            }



            //add by kexi
            if (!hasVerticalInited) {
                hasVerticalInited = true;
                currentY = yStartPos;
            }



            if (xOffset > 0) {
                xOffset += contentTextSize * 2;
                //另外加点留白.设留白两个字宽
                if (!isHorizontalRunning && !isVerticalRunning) {
                    isHorizontalRunning = true;
                    if (mIOnHorizontalScroll != null) {
                        mIOnHorizontalScroll.isHorizontalOnScroll(true);
                    }
                    startHorizontalScroll();
                    currnetX = 0;
                }
            } else {
                if (!isVerticalRunning) {
                    isVerticalRunning = true;
                    startVerticalInterval();
                    currnetX = 0;
                }
            }
            canvas.drawText(currentString, currnetX, currentY, contentPaint);
            canvas.drawText(nextString, 0, currentY + viewHeight, contentPaint);
        } else if (!TextUtils.isEmpty(singleText)) {
            viewHeight = getMeasuredHeight() + 10;
            viewWidth = getMeasuredWidth();
            Rect contentBound = new Rect();
            contentPaint.getTextBounds(singleText, 0, singleText.length(), contentBound);
            contentWidth = contentBound.width();
            xOffset = contentWidth - viewWidth;
            Paint.FontMetrics fontMetrics = contentPaint.getFontMetrics();
            int textHeight = (int) ((-fontMetrics.ascent - fontMetrics.descent) / 2);
            yStartPos = viewHeight / 2 + maxContentHeight / 4 + textHeight / 4;

            if (!hasInited) {
                hasInited = true;
                currnetX = 0;
                xStartPos = currnetX;
            }
            if (xOffset > 0) {
                xOffset += contentTextSize * 2;
                if (!isHorizontalRunning) {
                    isHorizontalRunning = true;
                    if (mIOnHorizontalScroll != null) {
                        mIOnHorizontalScroll.isHorizontalOnScroll(true);
                    }
                    startHorizontalLoop();
                }
            }
            canvas.drawText(singleText, currnetX, yStartPos, contentPaint);
        }
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

       // hasInited = false;
        /** 原代码是每次点击时，都将hasInited置为false，导致重新绘制的时候，
         控件的水平位置初始化了一遍，当控件在往右滚动的时候，
         就会滚动太多距离而出现字符消失或者留白过多的情况
         */

        /**
         * add by kexi
         * 判断是小组还是时空，由于小组在点击的时候会清空全部数据，而时空不会清空全部数据，
         * 所以小组点击的时候重新重新初始化一遍hasInited = false;否则会偶现点击后文字显示不全的情况
         */

        switch (marqueeTextViewType){
            case "Team":
                hasInited = false;
                break;
            case "Time":
                break;
            default:
                break;


        }

        //解决垂直方向一开始滚动卡顿问题
        hasVerticalInited = false;

        requestLayout();
        postInvalidate();
    }

    private void startVerticalInterval() {
        ValueAnimator verticalIntervalAnimator = ValueAnimator.ofFloat(0, 1);
        verticalIntervalAnimator.setDuration(verticalSwitchInterval);
        verticalIntervalAnimator.setInterpolator(new LinearInterpolator());
        verticalIntervalAnimator.start();
        verticalIntervalAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mIOnHorizontalScroll != null) {
                    mIOnHorizontalScroll.isHorizontalOnScroll(false);
                }
                startVerticalSwitch();
            }
        });
    }


    private void startVerticalSwitch() {
        ValueAnimator verticalSwitchAnimator = ValueAnimator.ofFloat(0, 1);
        verticalSwitchAnimator.setDuration(verticalSwitchSpeed);
        verticalSwitchAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        verticalSwitchAnimator.start();
        verticalSwitchAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currentY = (int) (yStartPos - value * viewHeight * 1);
                postInvalidate();
            }
        });
        verticalSwitchAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currnetIndex++;
                currentY = yStartPos;
                isVerticalRunning = false;
                postInvalidate();
            }
        });
    }

    private void startHorizontalScroll() {
        ValueAnimator horizontalScrollAnimator = ValueAnimator.ofFloat(0, 1);
        //在崩溃统计上看到值<0的bug
        if (horizontalScrollSpeed * xOffset / contentTextSize < 0) {
            isHorizontalRunning = false;
            return;
        }
        horizontalScrollAnimator.setDuration(horizontalScrollSpeed * xOffset / contentTextSize);
        horizontalScrollAnimator.setInterpolator(new LinearInterpolator());
        horizontalScrollAnimator.start();
        horizontalScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currnetX = (int) (-xOffset * value);
                postInvalidate();
            }
        });
        horizontalScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isHorizontalRunning = false;
                isVerticalRunning = true;
                startVerticalInterval();
                postInvalidate();
            }
        });
    }

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
        horizontalScrollAnimator.setInterpolator(new LinearInterpolator());
        horizontalScrollAnimator.start();
        horizontalScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currnetX = (int) (xStartPos - xOffset * value);
                postInvalidate();
            }
        });
        horizontalScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isHorizontalRunning = false;
                horizontalOriLeft = !horizontalOriLeft;
                xStartPos = currnetX;
                postInvalidate();
            }
        });
    }

    private IOnHorizontalScroll mIOnHorizontalScroll;

    public void setOnHorizontalScroll(IOnHorizontalScroll iOnHorizontalScroll) {
        this.mIOnHorizontalScroll = iOnHorizontalScroll;
    }

    public void setSingleText(String singleText) {
        this.singleText = singleText;
        postInvalidate();
    }

    public interface IOnHorizontalScroll {

        void isHorizontalOnScroll(boolean isHorizontalRunning);

    }

}