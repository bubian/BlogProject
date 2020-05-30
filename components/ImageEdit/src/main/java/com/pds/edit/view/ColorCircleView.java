package com.pds.edit.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.pds.edit.R;


public class ColorCircleView extends View{

    private static final String TAG = "ColorCircleView";

    private int inCircleColor = Color.BLACK;
    private int outCircleColor = Color.WHITE;


    private int inCircleR;
    private int outCircleStrokeWidth;

    private Context context;
    private Paint paint;
    private int width;
    private int height;
    private int dp_1;
    private int outCircleShadowColor;

    public ColorCircleView(Context context) {
        this(context,null);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = context.getResources();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView);

        inCircleR = typedArray.getDimensionPixelSize(R.styleable.ColorCircleView_in_circle_r,resources.getDimensionPixelSize(R.dimen.image_6));
        inCircleColor = typedArray.getColor(R.styleable.ColorCircleView_in_circle_color,Color.RED);
        outCircleStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ColorCircleView_out_circle_stroke_width,0);
        typedArray.recycle();

        width = height = resources.getDimensionPixelSize(R.dimen.image_23);
        dp_1 = resources.getDimensionPixelSize(R.dimen.image_1);
        outCircleShadowColor = resources.getColor(R.color.image_color_13000000);
    }

    public ColorCircleView setInCircleColor(@ColorRes int colorResId){
        this.inCircleColor = context.getResources().getColor(colorResId);
        return this;
    }


    public ColorCircleView setOutCircleColor(@ColorRes int colorResId){
        this.outCircleColor = context.getResources().getColor(colorResId);
        return this;
    }

    public ColorCircleView setInCircleR(int inCircleR){
        this.inCircleR = inCircleR;
        return this;
    }

    public ColorCircleView setOutCircleStrokeWidth(int outCircleStrokeWidth){
        this.outCircleStrokeWidth = outCircleStrokeWidth;
        return this;
    }

    public ColorCircleView setOutCircleShadowColor(int outCircleShadowColor){
        this.outCircleShadowColor = outCircleShadowColor;
        return this;
    }

    public ColorCircleView setWidth(int width){
        this.width = width;
        return this;
    }

    public ColorCircleView setHeight(int height){
        this.height = height;
        return this;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode){
            case MeasureSpec.EXACTLY:
                width = w;
                break;
        }

        switch (heightMode){
            case MeasureSpec.EXACTLY:
                height = h;
                break;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画内圆
        drawInCircle(canvas);
        if (outCircleStrokeWidth > 1){
            //画外环
            drawOutCircle(canvas);
        }
    }

    private void drawOutCircle(Canvas canvas) {
        paint.setColor(outCircleColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outCircleStrokeWidth);
        paint.setShadowLayer(dp_1,0,0,outCircleShadowColor);
        int r = width/2;
        canvas.drawCircle(r,r,inCircleR + outCircleStrokeWidth,paint);
    }

    private void drawInCircle(Canvas canvas) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(inCircleColor);
        paint.setStyle(Paint.Style.FILL);
        int r = width/2;
        canvas.drawCircle(r,r,+ inCircleR + outCircleStrokeWidth,paint);
    }

    public int getColor() {
        return inCircleColor;
    }

    public void doSelected() {
        setScaleX(1.1f);
        setScaleY(1.1f);
    }

    public void resetScale(){

        float s = getScaleX();
        if (s <= 1){
            return;
        }
        float q = 2 - s;
        setScaleX(q);
        setScaleY(q);
    }
}
