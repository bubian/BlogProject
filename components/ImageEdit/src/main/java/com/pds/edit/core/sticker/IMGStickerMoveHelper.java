package com.pds.edit.core.sticker;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class IMGStickerMoveHelper {

    private static final String TAG = "IMGStickerMoveHelper";

    private View mView;

    private float mX, mY;

    private static final Matrix M = new Matrix();

    public IMGStickerMoveHelper(View view) {
        mView = view;
    }

    public boolean onTouch(View v, MotionEvent event, RectF rectF,RectF textRectF) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                M.reset();
                M.setRotate(v.getRotation());
                return true;
            case MotionEvent.ACTION_MOVE:
               if (isMove(rectF,textRectF,event)){
                   return true;
               }
                float[] dxy = {event.getX() - mX, event.getY() - mY};
                M.mapPoints(dxy);
                v.setTranslationX(mView.getTranslationX() + dxy[0]);
                v.setTranslationY(mView.getTranslationY() + dxy[1]);
                return true;
        }
        return false;
    }

    private boolean isMove(RectF rectF,RectF textRectF,MotionEvent event){
        if (null != rectF && textRectF.top <= rectF.top && event.getY() <= mY){
            return true;
        }else if (null != rectF && textRectF.left<= rectF.left && event.getX() <= mX){
            return true;
        }else if (null != rectF && textRectF.right >= rectF.right && event.getX() >= mX){
            return true;
        }else if (null != rectF && textRectF.bottom >= rectF.bottom && event.getY() >= mY){
            return true;
        }else {
            return false;
        }
    }
}
