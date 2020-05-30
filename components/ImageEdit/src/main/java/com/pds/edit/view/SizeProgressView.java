package com.pds.edit.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorRes;

import com.pds.edit.R;

public class SizeProgressView extends View {
    private static final String TAG = "TouchProgressView";

    private Paint paint;

    private int pointRadius;//圆点默认半径,单位px
    private int pointColor = Color.RED;//圆点默认颜色

    private int lineHeight;//线默认高度,单位px
    private int progressWidth;
    private int lineColor = R.color.image_color_E8ECF4;//线默认颜色
    private int backgroundColor = Color.WHITE;

    private float progress = 0;
    private final int PROGRESS_MIN = 0;
    private final int PROGRESS_MAX = 1;
    private Resources resources;

    private OnProgressChangedListener progressChangedListener;
    private int maxCap;

    public interface OnProgressChangedListener {
        void onProgressChanged(View view, float progress);
    }

    public SizeProgressView(Context context) {
        this(context, null);
    }

    public SizeProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SizeProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resources = context.getResources();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mXfermode = new PorterDuffXfermode(mPorterDuffMode);

        lineHeight =  resources.getDimensionPixelSize(R.dimen.image_2);
        progressWidth = resources.getDimensionPixelSize(R.dimen.image_280);
        pointRadius = resources.getDimensionPixelSize(R.dimen.image_7);
        maxCap = resources.getDimensionPixelSize(R.dimen.image_4);
    }

    /**
     * 设置圆点半径
     *
     * @param radius
     */
    public void setPointRadius(final int radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("radius 不可以小于等于0");
        }

        if (getWidth() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (radius * 2 > getWidth()) {
                        throw new IllegalArgumentException("radius*2 必须小于 view.getWidth() == " + getWidth());
                    }
                    pointRadius = radius;
                }
            });
        } else {
            if (radius * 2 > getWidth()) {
                throw new IllegalArgumentException("radius*2 必须小于 view.getWidth() == " + getWidth());
            }
            this.pointRadius = radius;
        }
    }

    /**
     * 设置圆点颜色
     *
     * @param color
     */
    public void setPointColor( int color) {
        this.pointColor = color;
    }

    /**
     * 设置直线高度
     *
     * @param height
     */
    public void setLineHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("height 不可以小于等于0");
        }

        this.lineHeight = height;
    }

    /**
     * 设置直线颜色
     *
     * @param color
     */
    public void setLineColor(@ColorRes int color) {
        this.lineColor = color;
    }

    /**
     * 设置百分比
     *
     * @param progress
     */
    public void setProgress(float progress) {
        if (progress <  PROGRESS_MIN) progress = PROGRESS_MIN;
        else if (progress >  PROGRESS_MAX) progress = PROGRESS_MAX;
        this.progress = progress;
        invalidate();

        if (progressChangedListener != null) {
            progressChangedListener.onProgressChanged(this, progress);
        }
    }

    /**
     * 设置进度变化监听器
     *
     * @param onProgressChangedListener
     */
    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.progressChangedListener = onProgressChangedListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (x > getPaddingLeft() + progressWidth + 2*pointRadius + 6)return super.onTouchEvent(event);
        if (x <= pointRadius+getPaddingLeft()) {
            setProgress(PROGRESS_MIN);
            return true;
        } else if (x >= progressWidth + getPaddingLeft() + pointRadius) {
            setProgress(PROGRESS_MAX);
            return true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setProgress(calculateProgress(x));
                    return true;
                case MotionEvent.ACTION_MOVE:
                    setProgress(calculateProgress(x));
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        progressWidth = getWidth() - getPaddingRight() - getPaddingLeft() - 2*pointRadius;
    }

    private Xfermode mXfermode;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.DST_OUT;
    private RectF rectF = new RectF();
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getWidth();
        int height = getHeight();

        paint.reset();
        paint.setAntiAlias(true);

        paint.setColor(backgroundColor);

        rectF.set(0,0,width,height);
        canvas.drawRoundRect(rectF,height/2 ,height/2,paint);

        paint.reset();
        paint.setAntiAlias(true);

        int paddingLeft = getPaddingLeft();
        int lineX = paddingLeft + pointRadius;
        int lineY = height/2;

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(lineHeight);
        paint.setColor(pointColor);

        canvas.drawLine(lineX, lineY, getCx(), getHeight() / 2, paint);


        paint.setColor(resources.getColor(lineColor));


        canvas.drawLine(getCx(), lineY, getCx()+ (progressWidth*(1-progress)), getHeight() / 2, paint);

        paint.reset();
        paint.setAntiAlias(true);

        paint.reset();
        paint.setAntiAlias(true);

        float r = progress*maxCap + pointRadius;

        paint.setColor(backgroundColor);
        canvas.drawCircle(getCx(),height/2,r,paint);

        paint.setColor(pointColor);
        paint.setStrokeWidth(lineHeight);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getCx(), height/2, r, paint);
    }

    /**
     * 获取圆点的x轴坐标
     *
     * @return
     */
    private float getCx() {
        float cx = progressWidth;
        if (cx < 0) {
            throw new IllegalArgumentException("TouchProgressView 宽度不可以小于 2 倍 pointRadius");
        }
        return cx * progress + pointRadius + getPaddingLeft();
    }

    /**
     * 计算触摸点的百分比
     *
     * @param eventX
     * @return
     */
    private float calculateProgress(float eventX) {
        return (eventX - pointRadius -getPaddingLeft()) / progressWidth;
    }
}
