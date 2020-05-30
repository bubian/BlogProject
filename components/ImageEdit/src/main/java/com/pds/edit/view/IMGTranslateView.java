package com.pds.edit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.IMGPath;
import com.pds.edit.core.OverlayPath;
import com.pds.edit.core.homing.IMGHoming;
import com.pds.edit.core.manage.HistoryStepRecords;
import com.pds.edit.core.sticker.IMGSticker;
import com.pds.edit.core.util.ArrowUtils;
import com.pds.edit.core.util.IMGUtils;

import java.util.ArrayList;
import java.util.List;

public class IMGTranslateView extends IMGView {

    private Context context;
    private GestureDetector mGDetector;
    private ScaleGestureDetector mSGDetector;
    protected OverlayPath overlayPath = new OverlayPath();

    private static final int MIN_LEN = 10;
    private static final int MIN_ARC_WIDTH = 20;
    private int mPointerCount = 0;


    //涂鸦路径
    protected List<IMGPath> mDoodles = new ArrayList<>();
    protected List<IMGPath> mCircle = new ArrayList<>();
    protected List<IMGPath> mArrow = new ArrayList<>();
    protected List<IMGPath> mMosaics = new ArrayList<>();


    protected List<IMGPath> mPath = new ArrayList<>();


    //定义一个内存中图片，将他作为缓冲区
    Bitmap cacheBitmap = null;
    //定义缓冲区Cache的Canvas对象
    Canvas cacheCanvas = null;



    //马赛克路径
    protected static final int MIN_SIZE = 500;
    protected static final int MAX_SIZE = 10000;

    protected float scaleCap;

    protected HistoryStepRecords<IMGPath> stepRecords = new HistoryStepRecords<>();
    protected HistoryStepRecords<IMGSticker> stickerRecords = new HistoryStepRecords<>();


    public IMGTranslateView(Context context) {
        this(context,null);
    }

    public IMGTranslateView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IMGTranslateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
//        //创建一个与该VIew相同大小的缓冲区
//        cacheBitmap = Bitmap.createBitmap(1080,1920,Bitmap.Config.ARGB_8888);
//        //创建缓冲区Cache的Canvas对象
//        cacheCanvas = new Canvas();
//        //设置cacheCanvas将会绘制到内存的bitmap上
//        cacheCanvas.setBitmap(cacheBitmap);


        overlayPath.setMode(getMode());
        mGDetector = new GestureDetector(context, moveAdapter);
        mSGDetector = new ScaleGestureDetector(context, scaleGestureListener);
    }

    public void registerHistoryRecodesListener(HistoryStepRecords.HistoryStepRecordsChange historyStepRecordsChange){
        stepRecords.register(historyStepRecordsChange);

    }


    public boolean isFreezing() {
        return isFreezing;
    }

    public void setFreezing(boolean freezing) {
        if (freezing != isFreezing) {
            rotateStickers(freezing ? -getRotate() : getTargetRotate());
            isFreezing = freezing;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        doHomingRunable(event);
        return onTouch(event);
    }

    boolean onTouch(MotionEvent event) {
        if (isHoming()) return false;
        mPointerCount = event.getPointerCount();

        boolean handled = mSGDetector.onTouchEvent(event);

        IMGMode mode = getMode();
        if (mode == IMGMode.NONE || mode == IMGMode.CLIP) {
            handled |= onTouchNONE(event);
        } else if (mPointerCount > 1) {
//            onPathDone();
            handled |= onTouchNONE(event);
        } else {
            handled |= onTouchPath(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouchUp(getScrollX(), getScrollY());
                onHoming();
                break;
        }
        return handled;
    }

    private boolean onTouchNONE(MotionEvent event) {
        return mGDetector.onTouchEvent(event);
    }

    private boolean onTouchPath(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return onPathBegin(event);
            case MotionEvent.ACTION_MOVE:
                if (mPointerCount > 1)return false;
                return onPathMove(event);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return (overlayPath.isIdentity(event.getPointerId(0)) && onPathDone());
        }
        return false;
    }


    float startX  = 0;
    float startY  = 0;

    private boolean onPathBegin(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();
        if (getMode() == IMGMode.CIRCLE){
            overlayPath.reset();
        } else{
            overlayPath.reset(event.getX(), event.getY());
        }
        overlayPath.setIdentity(event.getPointerId(0));
        return true;
    }



    private RectF tempRectF = new RectF();

    private void point(RectF rectF,float left,float top,float right,float bottom){
        rectF.left = left;
        rectF.top = top;
        rectF.right = right;
        rectF.bottom = bottom;
    }

    private boolean onPathMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (getMode() == IMGMode.CIRCLE){

            float dX = Math.abs(x- startX);
            float dY = Math.abs(y - startY);
            if ((dX > MIN_LEN || dY > MIN_LEN) &&  overlayPath.isIdentity(event.getPointerId(0))){


                if (x < startX) {
                    if (event.getY() < startY) {
                        point(tempRectF,x,y,startX,startY);
                    } else {
                        point(tempRectF,x,startY,startX,y);
                    }
                } else {
                    if (event.getY() < startY) {
                        point(tempRectF,startX,y,x,startY);
                    } else {
                        point(tempRectF,startX,startY,x,y);
                    }
                }

//                if (x > startX){
//                    point(tempRectF,startX,y +  dY/4,x,y+ 3*dY/4);
//                }else {
//                    point(tempRectF,x,y - dY/3,startX,y+dY/3);
//                }

                overlayPath.getPath().reset();
                overlayPath.getPath().addArc(tempRectF,0,360);
                invalidate();
                return true;
            }

        }else if (getMode() == IMGMode.ARROW){
            float dX = Math.abs(x- startX);
            float dY = Math.abs(y - startY);

            if ((dX > MIN_LEN || dY > MIN_LEN) &&  overlayPath.isIdentity(event.getPointerId(0))){
                ArrowUtils.getArrowPath(startX,startY,x,y, 5,getScale(),overlayPath.getPath());
//                overlayPath.transform(1/getScale());
                invalidate();
                return true;
            }

        }else {
            if (overlayPath.isIdentity(event.getPointerId(0))) {
                overlayPath.lineTo(event.getX(), event.getY());
                invalidate();
                return true;
            }
        }
        return false;
    }

    protected void onCacheDraw(IMGMode mode,IMGPath path) {
    }


    private boolean onPathDone() {
        if (!overlayPath.isEmpty() && mPointerCount == 1){
            IMGPath path = overlayPath.toPath();
            addPath(path, getScrollX(), getScrollY());
            overlayPath.reset();
            invalidate();
//            onCacheDraw(overlayPath.getMode(),path);
            return true;
        }
        return false;
    }

    public void addPath(IMGPath path, float sx, float sy) {
        if (path == null) return;

        float scale = 1f / getScale();
        M.setTranslate(sx, sy);
        M.postRotate(-getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.postTranslate(-mFrame.left, -mFrame.top);
        M.postScale(scale, scale);
        path.transform(M);

        IMGMode mode = path.getMode();
        switch (mode) {
            case DOODLE:
                addPath(path,mode,scale,path.getRealPaintSize());
                break;
            case MOSAIC:
                addPath(path,mode,scale,path.getRealPaintSize());
                break;
            case CIRCLE:
                addPath(path,mode,scale,IMGPath.BASE_PAINT_WIDTH);
                break;
            case ARROW:
                addPath(path,mode,scale,path.getRealPaintSize());
                break;
        }
    }

    private void addPath(IMGPath path,IMGMode mode,float scale,float width){
        path.setWidth(width * scale);
        path.setMode(mode);
        mPath.add(path);
        stepRecords.clearForwardStack();
        stepRecords.addRecodeToBackStack(path);
    }

    private void addPath(IMGPath path,List<IMGPath> paths,float scale,float width){
        path.setWidth(width * scale);
        paths.add(path);
        stepRecords.clearForwardStack();
        stepRecords.addRecodeToBackStack(path);
    }


    protected boolean onScrollTo(int x, int y) {
        if (getScrollX() != x || getScrollY() != y) {
            scrollTo(x, y);
            return true;
        }
        return false;
    }

    private boolean doScroll(float dx, float dy) {
        IMGHoming homing = onScroll(getScrollX(), getScrollY(), -dx, -dy);
        if (homing != null) {
            toApplyHoming(homing);
            return true;
        }
        return onScrollTo(getScrollX() + Math.round(dx), getScrollY() + Math.round(dy));
    }

    public IMGHoming onScroll(float scrollX, float scrollY, float dx, float dy) {
        if (mMode == IMGMode.CLIP) {
            mClipWin.setShowShade(false);
            if (mAnchor != null) {
                mClipWin.onScroll(mAnchor, dx, dy);

                RectF clipFrame = new RectF();
                M.setRotate(getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                M.mapRect(clipFrame, mFrame);

                RectF frame = mClipWin.getOffsetFrame(scrollX, scrollY);
                IMGHoming homing = new IMGHoming(scrollX, scrollY, getScale(), getTargetRotate());
                homing.rConcat(IMGUtils.fillHoming(frame, clipFrame, mClipFrame.centerX(), mClipFrame.centerY()));
                return homing;
            }
        }
        return null;
    }

    public float getTargetRotate() {
        return mTargetRotate;
    }

    public void setTargetRotate(float targetRotate) {
        this.mTargetRotate = targetRotate;
    }

    /**
     * 在当前基础上旋转
     */
    public void rotate(int rotate) {
        mTargetRotate = Math.round((mRotate + rotate) / 90f) * 90;
        mClipWin.reset(mClipFrame, getTargetRotate());
    }

    public float getRotate() {
        return mRotate;
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
    }

    public float getScale() {
        return 1f * mFrame.width() / mImage.getWidth();
    }

    public void setScale(float scale) {
        setScale(scale, mClipFrame.centerX(), mClipFrame.centerY());
    }

    public void setScale(float scale, float focusX, float focusY) {
        doScale(scale / getScale(), focusX, focusY);
    }

    public void doScale(float factor, float focusX, float focusY) {

        if (factor == 1f) return;

        if (Math.max(mClipFrame.width(), mClipFrame.height()) >= MAX_SIZE
                || Math.min(mClipFrame.width(), mClipFrame.height()) <= MIN_SIZE) {
            factor += (1 - factor) / 2;
        }

        M.setScale(factor, factor, focusX, focusY);
        M.mapRect(mFrame);
        M.mapRect(mClipFrame);

        // 修正clip 窗口
        if (!mFrame.contains(mClipFrame)) {
            // TODO
            mClipFrame.intersect(mFrame);
        }

        for (IMGSticker sticker : mBackStickers) {
            M.mapRect(sticker.getFrame());
            float tPivotX = sticker.getX() + sticker.getPivotX();
            float tPivotY = sticker.getY() + sticker.getPivotY();
            sticker.addScale(factor);
            sticker.setX(sticker.getX() + sticker.getFrame().centerX() - tPivotX);
            sticker.setY(sticker.getY() + sticker.getFrame().centerY() - tPivotY);
        }
    }

    protected void rotateStickers(float rotate) {
        M.setRotate(rotate, mClipFrame.centerX(), mClipFrame.centerY());
        for (IMGSticker sticker : mBackStickers) {
            M.mapRect(sticker.getFrame());
            sticker.setRotation(sticker.getRotation() + rotate);
            sticker.setX(sticker.getFrame().centerX() - sticker.getPivotX());
            sticker.setY(sticker.getFrame().centerY() - sticker.getPivotY());
        }
    }

    private GestureDetector.SimpleOnGestureListener moveAdapter = new  GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return doScroll(distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // TODO
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener(){

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mPointerCount > 1) {
                scaleCap = detector.getScaleFactor() -1;
                doScale(detector.getScaleFactor(),
                        getScrollX() + detector.getFocusX(),
                        getScrollY() + detector.getFocusY());
                invalidate();
                return true;
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (mPointerCount > 1) {
                scaleCap = detector.getScaleFactor() -1;
                return true;
            }
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaleCap = 0;

        }
    };

    protected void toApplyHoming(IMGHoming homing){}
    protected void doHomingRunable(MotionEvent event){}
    protected void onHoming(){}
}
