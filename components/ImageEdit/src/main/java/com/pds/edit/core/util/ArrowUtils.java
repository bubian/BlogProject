package com.pds.edit.core.util;

import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;

public class ArrowUtils {


    private static final double KEY_POINT_LEN1 = 70;
    private static final double KEY_POINT_LEN2 = 2;
    private static final double KEY_POINT_LEN3 = 30 * Math.PI/ 180;
    private static final double KEY_POINT_ANGLE1 = 18 * Math.PI/ 180;
    private static final double KEY_POINT_ANGLE2 = 90 * Math.PI/ 180;
    private static final double KEY_POINT_ANGLE3 = 90* Math.PI/ 180;
    private static final double KEY_POINT_RATIO1 = 0.2;
    private static final double KEY_POINT_RATIO2 = 0.157;
    private static final double KEY_POINT_RATIO3 = 0.023;

    public static Path getArrowPath(MotionEvent event,float startX,float  startY,Path path){

        float endX = event.getX();
        float endY = event.getY();

        path.reset();
        //配置箭头的6个点位
        double len1 = KEY_POINT_LEN1;
        double len2 = KEY_POINT_LEN2;
        double len3 = KEY_POINT_LEN3;

        double len = Math.sqrt(Math.pow((endX - startX), 2) + Math.pow((endY - startY), 2));
        if (len * KEY_POINT_RATIO1 < KEY_POINT_LEN1) {
            len1 = len * KEY_POINT_RATIO1;
        }
        if (len * KEY_POINT_RATIO2 < KEY_POINT_LEN2) {
            len2 = len * KEY_POINT_RATIO2;
        }
        if (len * KEY_POINT_RATIO3 < KEY_POINT_LEN3) {
            len3 = len * KEY_POINT_RATIO3;
        }

        Point point11 = rotateVecWithPx(endX - startX,endY - startY,KEY_POINT_ANGLE1,len1);
        Point point12 = rotateVecWithPx(endX - startX,endY - startY,-KEY_POINT_ANGLE1,len1);

        Point point21 = rotateVecWithPx(endX - startX,endY - startY,KEY_POINT_ANGLE2,len2);
        Point point22 = rotateVecWithPx(endX - startX,endY - startY,-KEY_POINT_ANGLE2,len2);


        Point point31 = rotateVecWithPx(endX - startX,endY - startY,KEY_POINT_ANGLE3,len3);
        Point point32 = rotateVecWithPx(endX - startX,endY - startY,-KEY_POINT_ANGLE3,len3);

        float x11 = endX - point11.x;
        float y11 = endY - point11.y;
        float x12 = endX - point12.x;
        float y12 = endY - point12.y;
        float x21 = endX - point21.x;
        float y21 = endY - point21.y;
        float x22 = endX - point22.x;
        float y22 = endY - point22.y;
        float x31 = startX - point31.x;
        float y31 = startY - point31.y;
        float x32 = startX - point32.x;
        float y32 = startY - point32.y;


        path.moveTo(startX,startY);
        path.lineTo(x11,y11);
        path.lineTo(x21,y21);
        path.lineTo(x32,y32);
        path.lineTo(x31,y31);
        path.lineTo(x22,y22);
        path.lineTo(x12,y12);

        return path;
    }



    private static Point rotateVecWithPx(float x,float y,double ang,double newLen){
        double vx = x * Math.cos(ang) - y * Math.sin(ang);
        double vy = x * Math.sin(ang) + y * Math.cos(ang);
        double d = Math.sqrt(vx * vx + vy * vy);
        vx = vx / d * newLen;
        vy = vy / d * newLen;

        return new Point((int)vx,(int)vy);

    }


    public static Path getArrowPath(float sx, float sy, float ex, float ey, int width, float scale, Path triangle) {
        triangle.reset();
        int size = 5;
        int count = 20;
        switch (width) {
            case 0:
                size = 5;
                count = 20;
                break;
            case 5:
                size = 8;
                count = 30;
                break;
            case 10:
                size = 11;
                count = 40;
                break;
        }

        if (scale < 0.5){
            size = (int) (size * scale*2);
            count = (int) (count * scale/2);
        }else if (scale < 0.8){
            size = (int) (size * scale*3/2);
            count = (int) (count * scale*3/2);
        } else if (scale > 2){
            size = (int) (size * scale*1/2);
            count = (int) (count * scale*1/2);
        }else if (scale >= 3){
            size = (int) (size * scale*1/3);
            count = (int) (count * scale*1/3);
        }else if (scale >= 4){
            size = (int) (size * scale*1/4);
            count = (int) (count * scale*1/4);
        }else if (scale >= 5){
            size = (int) (size * scale*1/5);
            count = (int) (count * scale*1/5);
        } else if (scale >= 6){
            size = (int) (size * scale*1/6);
            count = (int) (count * scale*1/6);
        }

        float x = ex - sx;
        float y = ey - sy;
        double d = x * x + y * y;
        double r = Math.sqrt(d);
        float zx = (float) (ex - (count * x / r));
        float zy = (float) (ey - (count * y / r));
        float xz = zx - sx;
        float yz = zy - sy;
        double zd = xz * xz + yz * yz;
        double zr = Math.sqrt(zd);


        triangle.moveTo(sx, sy);
        triangle.lineTo((float) (zx + size * yz / zr), (float) (zy - size * xz / zr));
        triangle.lineTo((float) (zx + size * 2 * yz / zr), (float) (zy - size * 2 * xz / zr));
        triangle.lineTo(ex, ey);
        triangle.lineTo((float) (zx - size * 2 * yz / zr), (float) (zy + size * 2 * xz / zr));
        triangle.lineTo((float) (zx - size * yz / zr), (float) (zy + size * xz / zr));
        triangle.close();

        return triangle;
    }


}
