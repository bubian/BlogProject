package com.pds.edit.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

public class IMGPath {

    protected Path path;

    private int color = Color.RED;

    private float width = BASE_PAINT_WIDTH;

    private IMGMode mode = IMGMode.DOODLE;

    public static final float BASE_PAINT_WIDTH = 10f;
    private static final float CAP_PAINT_SIZE = 40f;
    private float paintSizeRate = 0;

    public IMGPath() {
        this(new Path());
    }

    public IMGPath(Path path) {
        this(path, IMGMode.DOODLE);
    }

    public IMGPath(Path path, IMGMode mode) {
        this(path, mode, Color.RED);
    }

    public IMGPath(Path path, IMGMode mode, int color) {
        this(path, mode, color, BASE_PAINT_WIDTH);
    }

    public IMGPath(Path path, IMGMode mode, int color, float width) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        if (mode == IMGMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    public IMGPath(Path path, IMGMode mode, int color, float width,float paintSizeRate) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        this.paintSizeRate = paintSizeRate;
        if (mode == IMGMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public IMGMode getMode() {
        return mode;
    }

    public void setMode(IMGMode mode) {
        this.mode = mode;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setPaintSizeRate(float paintSizeRate){
        this.paintSizeRate = paintSizeRate;
    }

    public float getPaintSizeRate(){
        return paintSizeRate;
    }

    public float getWidth() {
        return width;
    }

    public void onDrawDoodle(Canvas canvas, Paint paint,float scale) {
        if (mode == IMGMode.DOODLE) {
            paint.reset();
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setPathEffect(new CornerPathEffect(getRealPaintSize()));
            paint.setColor(color);
            paint.setStrokeWidth(width*(1+scale));
            // rewind
            canvas.drawPath(path, paint);

        }
    }

    public void onDrawMosaic(Canvas canvas, Paint paint,float scale) {
        if (mode == IMGMode.MOSAIC) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setPathEffect(new CornerPathEffect(width*(1+scale)));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(width*(1+scale));
            canvas.drawPath(path, paint);
        }
    }

    public void onDrawCircle(Canvas canvas, Paint paint,float scale) {
        if (mode == IMGMode.CIRCLE) {
            paint.reset();
            paint.setColor(color);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(width*(1+scale));
            canvas.drawPath(path,paint);
        }
    }

    public void onDrawArrow(Canvas canvas, Paint paint,float scale) {
        if (mode == IMGMode.ARROW) {
            paint.reset();
            paint.setAntiAlias(true);
            paint.setColor(color);

//            matrix.reset();
//            matrix.postScale(1+scale,1+scale);
//            transform(matrix);
            canvas.drawPath(path, paint);
        }
    }

    public void transform(Matrix matrix) {
        path.transform(matrix);
    }

    public int getRealPaintSize(){
        return (int) (BASE_PAINT_WIDTH + CAP_PAINT_SIZE*paintSizeRate);
    }
}
