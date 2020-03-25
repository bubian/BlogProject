package com.pds.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.IntDef;

import com.pds.ui.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 跑马灯（横行）
 * https://github.com/dalong982242260/AndroidMarqueeView
 *
 * @author hmy
 */
public class MarqueeHorizontalView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    @IntDef({
            LEFT, RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    private static final int START = 0;
    private static final int END = 1;

    @IntDef({
            START, END
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface StartPoint {
    }

    public Context mContext;

    private float mTextSize = 100; //字体大小

    private int mTextColor = Color.RED; //字体的颜色

    private boolean mIsRepeat;//是否重复滚动

    private int mStartPoint;// 开始滚动的位置  0是从最左面开始    1是从最末尾开始

    private int mDirection;//滚动方向 0 向左滚动   1向右滚动

    private int mSpeed;//滚动速度

    private SurfaceHolder mHolder;

    private TextPaint mTextPaint;

    private MarqueeViewThread mThread;

    private String mMargueeString;

    private int mTextWidth = 0, mTextHeight = 0;

    private int mShadowColor = Color.BLACK;

    public int mCurrentX = 0;// 当前x的位置

    public int mSepX = 5;//每一步滚动的距离

    public MarqueeHorizontalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeHorizontalView(Context context) {
        this(context, null);
    }

    public MarqueeHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeHorizontalView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.MarqueeHorizontalView_mvTextColor, Color.RED);
        mTextSize = a.getDimension(R.styleable.MarqueeHorizontalView_mvTextSize, 48);
        mIsRepeat = a.getBoolean(R.styleable.MarqueeHorizontalView_isRepeat, true);
        mStartPoint = a.getInt(R.styleable.MarqueeHorizontalView_startPoint, END);
        mDirection = a.getInt(R.styleable.MarqueeHorizontalView_direction, LEFT);
        mSpeed = a.getInt(R.styleable.MarqueeHorizontalView_speed, 20);
        a.recycle();

        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        setZOrderOnTop(true);//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//使窗口支持透明度
    }

    public void setDirection(@Direction int direction) {
        mDirection = direction;
    }

    public void setIsRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public void setStartPoint(@StartPoint int startPoint) {
        mStartPoint = startPoint;
    }

    public void setText(String msg) {
        setText(msg, 0, 0);
    }

    public void setText(String msg, int textColor, int textSize) {
        if (textColor != 0) {
            mTextColor = textColor;
        }
        if (textSize != 0) {
            mTextSize = textSize;
        }
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg);
        }
    }

    protected void measurementsText(String msg) {
        mMargueeString = msg;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(0.5f);
        mTextPaint.setFakeBoldText(true);
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
//        mTextPaint.setShadowLayer(5, 3, 3, mShadowColor);
        mTextWidth = (int) mTextPaint.measureText(mMargueeString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = (int) fontMetrics.bottom;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (mStartPoint == 0)
            mCurrentX = 0;
        else
            mCurrentX = width - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mThread != null)
            mThread.isRun = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mThread != null)
            mThread.isRun = false;
    }

    /**
     * 开始滚动
     */
    public void startScroll() {

        if (mThread != null && mThread.isRun)
            return;
        mThread = new MarqueeViewThread(mHolder);//创建一个绘图线程
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (mThread != null) {
            mThread.isRun = false;
            mThread.interrupt();
        }
        mThread = null;
    }

    /**
     * 线程
     */
    class MarqueeViewThread extends Thread {

        private SurfaceHolder holder;

        public boolean isRun;//是否在运行


        public MarqueeViewThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        public void onDraw() {
            try {
                synchronized (holder) {
                    if (TextUtils.isEmpty(mMargueeString)) {
                        Thread.sleep(1000);//睡眠时间为1秒
                        return;
                    }
                    Canvas canvas = holder.lockCanvas();
                    int paddingLeft = getPaddingLeft();
                    int paddingTop = getPaddingTop();
                    int paddingRight = getPaddingRight();
                    int paddingBottom = getPaddingBottom();

                    int contentWidth = getWidth() - paddingLeft - paddingRight;
                    int contentHeight = getHeight() - paddingTop - paddingBottom;

                    int centeYLine = paddingTop + contentHeight / 2;//中心线

                    if (mDirection == 0) {//向左滚动
                        if (mCurrentX <= -mTextWidth) {
                            if (!mIsRepeat) {//如果是不重复滚动
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            mCurrentX = contentWidth;
                        } else {
                            mCurrentX -= mSepX;
                        }
                    } else {//  向右滚动
                        if (mCurrentX >= contentWidth) {
                            if (!mIsRepeat) {//如果是不重复滚动
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            mCurrentX = -mTextWidth;
                        } else {
                            mCurrentX += mSepX;
                        }
                    }

                    if (canvas != null) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                        canvas.drawText(mMargueeString, mCurrentX, centeYLine + dip2px(getContext(), mTextHeight) / 2, mTextPaint);
                        holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                    }
                    int a = mTextWidth / mMargueeString.trim().length();
                    int b = a / mSepX;
                    int c = mSpeed / b == 0 ? 1 : mSpeed / b;

                    Thread.sleep(c);//睡眠时间为移动的频率


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (isRun) {
                onDraw();
            }

        }

    }

    public static final int ROLL_OVER = 100;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ROLL_OVER:
                    stopScroll();
                    if (mOnMarqueeListener != null) {
                        mOnMarqueeListener.onRollOver();
                    }
                    break;
            }
        }
    };

    /**
     * dip转换为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void reset() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (mStartPoint == 0)
            mCurrentX = 0;
        else
            mCurrentX = contentWidth;
    }

    public void onStart() {
        if (getVisibility() == VISIBLE) {
            startScroll();
        }
    }

    public void onStop() {
        stopScroll();
    }

    /**
     * 滚动回调
     */
    public interface OnMarqueeListener {
        void onRollOver();//滚动完毕
    }

    OnMarqueeListener mOnMarqueeListener;

    public void setOnMargueeListener(OnMarqueeListener mOnMargueeListener) {
        this.mOnMarqueeListener = mOnMargueeListener;
    }
}
