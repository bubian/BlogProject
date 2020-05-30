package com.pds.edit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.edit.core.IMGMode;

public class CustomOverlayView extends View{
    private IMGMode mode;
    private int color = Color.RED;
    private int size = 20;

    private Paint paint;

    public CustomOverlayView(Context context) {
        this(context,null);
    }

    public CustomOverlayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        color = getResources().getColor(R.color.image_color_F05F5F);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void mode(IMGMode mode){
        this.mode = mode;
    }

    public void color(int mode){
        this.color = color;
    }

    public void size(int size){
        this.size = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(100,100);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color);
        canvas.drawCircle(300,300,100,paint);
        if (IMGMode.ARROW == mode){
            doArrowDraw(canvas);
        }else if (IMGMode.CIRCLE == mode){
            doCircleDraw(canvas);
        }

    }

    private void doCircleDraw(Canvas canvas) {
//        paint.setColor(color);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(size);

    }

    private void doArrowDraw(Canvas canvas) {


    }
}
