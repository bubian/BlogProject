package com.pds.ui.view.refresh;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class IOSLoadingView extends View{


    private static final String TAG = IOSLoadingView.class.getSimpleName();
    /**
     * view宽度
     */
    private int width;
    /**
     * view高度
     */
    private int height;
    /**
     * 菊花的矩形的宽
     */
    private int widthRect;
    /**
     * 菊花的矩形的宽
     */
    private int heigheRect;
    /**
     * 菊花绘制画笔
     */
    private Paint rectPaint;
    /**
     * 循环绘制位置
     */
    private int pos = 0;
    /**
     * 菊花矩形
     */
    private Rect rect;
    /**
     * 循环颜色
     */
//    private String[] color = {"#bbbbbb", "#aaaaaa", "#999999", "#888888", "#777777", "#666666",};
    private String[] colorShrink = {"#eeffffff", "#ccffffff", "#aaffffff", "#80ffffff", "#70ffffff", "#50ffffff"};
    private String[] colorDrag = {"#ffffffff", "#ffffffff", "#ffffffff", "#ffffffff", "#ffffffff", "#ffffffff"};//拖拽时的颜色


    private int rotatePosition = 0;//画布旋转的位置

    private boolean isShrinking = false;//是否在缩小

    public boolean isShrinking() {
        return isShrinking;
    }

    public void setShrinking(boolean shrinking) {
        isShrinking = shrinking;
    }

    public IOSLoadingView(Context context) {
        this(context, null);
    }

    public IOSLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IOSLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }


    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //根据个人习惯设置  这里设置  如果是wrap_content  则设置为宽高200
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            width = dip2px(getContext(),15);
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = Math.min(width, height);
        }

        widthRect = width / 9;   //菊花矩形的宽
        heigheRect = 2 * widthRect;  //菊花矩形的高

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制部分是关键了，菊花花瓣矩形有12个，我们不可能去一个一个的算出所有的矩形坐标，我们可以考虑
        //旋转下面的画布canvas来实现绘制，每次旋转30度
        //首先定义一个矩形
        if (rect == null) {
            rect = new Rect((width - widthRect) / 2, 0, (width + widthRect) / 2, heigheRect);

        }

        //       0  1  2  3  4  5  6  7  8  9  10  11   i的值
        // ————————————————————————————————————————————————————————
        //  0   ‖ 0 | 1 | 2 | 3 | 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 ‖
        //  1   ‖ 5 | 0 | 1 | 2 | 3 | 4 | 5 | 5 | 5 | 5 | 5 | 5 ‖
        //  2   ‖ 5 | 5 | 0 | 1 | 2 | 3 | 4 | 5 | 5 | 5 | 5 | 5 ‖
        //  3   ‖ 5 | 5 | 5 | 0 | 1 | 2 | 3 | 4 | 5 | 5 | 5 | 5 ‖
        //  4   ‖ 5 | 5 | 5 | 5 | 0 | 1 | 2 | 3 | 4 | 5 | 5 | 5 ‖
        //  5   ‖ 5 | 5 | 5 | 5 | 5 | 0 | 1 | 2 | 3 | 4 | 5 | 5 ‖
        //  6   ‖ 5 | 5 | 5 | 5 | 5 | 5 | 0 | 1 | 2 | 3 | 4 | 5 ‖
        //  7   ‖ 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 | 1 | 2 | 3 | 4 ‖
        //  8   ‖ 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 | 1 | 2 | 3 ‖
        //  9   ‖ 3 | 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 | 1 | 2 ‖
        //  10   ‖ 2 | 3 | 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 | 1 ‖
        //  11   ‖ 1 | 2 | 3 | 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 ‖
        //  pos的值

        for (int i = 0; i <=rotatePosition; i++) {
            if (i - pos >= 5) {
                if (isShrinking())
                    rectPaint.setColor(Color.parseColor(colorShrink[5]));
                else
                    rectPaint.setColor(Color.parseColor(colorDrag[5]));

            } else if (i - pos >= 0 && i - pos < 5) {
                if (isShrinking())
                    rectPaint.setColor(Color.parseColor(colorShrink[i - pos]));
                else
                    rectPaint.setColor(Color.parseColor(colorDrag[i - pos]));
            } else if (i - pos >= -7 && i - pos < 0) {
                if (isShrinking())
                    rectPaint.setColor(Color.parseColor(colorShrink[5]));
                else
                    rectPaint.setColor(Color.parseColor(colorDrag[5]));
            } else if (i - pos >= -11 && i - pos < -7) {
                if (isShrinking())
                    rectPaint.setColor(Color.parseColor(colorShrink[12 + i - pos]));
                else
                    rectPaint.setColor(Color.parseColor(colorDrag[12 + i - pos]));

            }

            canvas.drawRect(rect, rectPaint);  //绘制
            canvas.rotate(30, width / 2, width / 2);    //旋转
        }

//        pos++;
//        if (pos > 11) {
//            pos = 0;
//        }

        //postInvalidateDelayed(100);  //一个周期用时1200

    }

    private ValueAnimator valueAnimator ;
    //private RotateAnimation rotateAnimation ;
    private ObjectAnimator rotateAnimation;


    public void setStartAnimal() {
        cancelAnimator();
        valueAnimator = ValueAnimator.ofInt(0, 11);
        valueAnimator.setDuration(1);
        //valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pos = (int) animation.getAnimatedValue();

                invalidate();
            }
        });
        valueAnimator.start();


//        rotateAnimation  = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        LinearInterpolator lin = new LinearInterpolator();
//        rotateAnimation.setInterpolator(lin);
//        rotateAnimation.setDuration(1000);//设置动画持续周期
//        rotateAnimation.setRepeatCount(-1);//设置重复次数
//        rotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//       // rotateAnimation.setStartOffset(10);//执行前的等待时间
//        this.setAnimation(rotateAnimation);
//        this.startAnimation(rotateAnimation);

        rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
        rotateAnimation.setDuration(800);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setAutoCancel(true);

        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        rotateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                setRotation(0);
            }
        });

        rotateAnimation.start();

    }


    public void startRotateAnimation(){
        rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
        rotateAnimation.setDuration(800);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setAutoCancel(true);

        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);

        rotateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                setRotation(0);
            }
        });

        rotateAnimation.start();
        isRotateStarted = true;
    }




    public void cancelAnimator(){
        if (null!=valueAnimator&&valueAnimator.isStarted()){
            valueAnimator.cancel();
            valueAnimator = null;
        }
        if (null!=rotateAnimation&&rotateAnimation.isStarted()){
            this.clearAnimation();
            rotateAnimation.cancel();
            rotateAnimation = null;
        }
    }


    public void canvasRotate(int index){
        pos = 0;//初始化一下，否则由于旋转动画导致pos变化
        rotatePosition = index;
        invalidate();

    }

    private boolean isRotateStarted =false;

    public boolean isRotateStarted() {
        return isRotateStarted;
    }

    public void setRotateStarted(boolean rotateStarted) {
        isRotateStarted = rotateStarted;
    }
}
