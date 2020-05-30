package com.pds.edit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.IMGPath;
import com.pds.edit.core.IMGText;
import com.pds.edit.core.sticker.IMGSticker;
import com.pds.edit.core.sticker.IMGStickerPortrait;

import java.util.List;

public class IMGDrawView extends IMGHomingView implements IMGStickerPortrait.Callback{

    private Paint mDoodlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMosaicPaint;
    private Paint mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private IMGMode mPreMode = IMGMode.NONE;

    private Bitmap mMosaicImage;
    protected RectF mTempClipFrame = new RectF();
    protected Paint mPaint, mShadePaint;

    protected static final int COLOR_SHADE = 0xCC000000;

    public IMGDrawView(Context context) {
        this(context,null);
    }

    public IMGDrawView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IMGDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isHardwareAccelerated()){
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
        if (mMode == IMGMode.CLIP) {
            initShadePaint();
        }
        mShade.setFillType(Path.FillType.WINDING);
        // Doodle&Mosaic 's paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(overlayPath.getRealPaintSize());
        mPaint.setColor(Color.RED);
        mPaint.setPathEffect(new CornerPathEffect(overlayPath.getRealPaintSize()));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        initPaint();
    }

    private void initShadePaint() {
        if (mShadePaint == null) {
            mShadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadePaint.setColor(COLOR_SHADE);
            mShadePaint.setStyle(Paint.Style.FILL);
        }
    }

    private void makeMosaicBitmap() {
        if (mMosaicImage != null || mImage == null || mImage.isRecycled()) {
            return;
        }

        if (mMode == IMGMode.MOSAIC) {
            int w = Math.round(mImage.getWidth() / 64f);
            int h = Math.round(mImage.getHeight() / 64f);

            w = Math.max(w, 8);
            h = Math.max(h, 8);

            // 马赛克画刷
            if (mMosaicPaint == null) {
                mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mMosaicPaint.setFilterBitmap(false);
                mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }

            mMosaicImage = Bitmap.createScaledBitmap(mImage, w, h, false);
        }
    }

    public void setPenColor(int color) {
        overlayPath.setColor(color);
    }

    public void setMode(IMGMode mode) {
        // 保存现在的编辑模式
        mPreMode = getMode();
        // 设置新的编辑模式
        if (this.mMode == mode) return;
        moveToBackground(mForeSticker);

        if (mode == IMGMode.CLIP) {
            setFreezing(true);
        }

        this.mMode = mode;
        if (mMode == IMGMode.CLIP) {
            // 初始化Shade 画刷
            initShadePaint();

            // 备份裁剪前Clip 区域
            mBackupClipRotate = getRotate();
            mBackupClipFrame.set(mClipFrame);

            float scale = 1 / getScale();
            M.setTranslate(-mFrame.left, -mFrame.top);
            M.postScale(scale, scale);
            M.mapRect(mBackupClipFrame);

            // 重置裁剪区域
            mClipWin.reset(mClipFrame, getTargetRotate());
        } else {

            if (mMode == IMGMode.MOSAIC) {
                makeMosaicBitmap();
            }

            mClipWin.setClipping(false);
        }
        overlayPath.setMode(mode);
        // 矫正区域
        removeCallbacks(homingRunnable);
        postDelayed(homingRunnable, 200);
//        onHoming();
    }

    public void resetClip() {
        setTargetRotate(getRotate() - getRotate() % 360);
        mClipFrame.set(mFrame);
        mClipWin.reset(mClipFrame, getTargetRotate());
        onHoming();
    }

    public void doClip() {
        clip(getScrollX(), getScrollY());
        setMode(mPreMode);
        onHoming();
    }

    private void initPaint() {
        // 涂鸦画刷
        mDoodlePaint.setStyle(Paint.Style.STROKE);
        mDoodlePaint.setStrokeWidth(overlayPath.getRealPaintSize());
        mDoodlePaint.setColor(Color.RED);
        mDoodlePaint.setPathEffect(new CornerPathEffect(overlayPath.getRealPaintSize()));
        mDoodlePaint.setStrokeCap(Paint.Cap.ROUND);
        mDoodlePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void cancelClip() {
        toBackupClip();
        setMode(mPreMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawImages(canvas);
    }


    private void onDrawExtImages(Canvas canvas) {
        if (mImage == null || mImage.isRecycled()) {
            return;
        }
        canvas.save();
        RectF clipFrame = getClipFrame();
        canvas.rotate(getRotate(), clipFrame.centerX(), clipFrame.centerY());
        //画图片
        onDrawPicture(canvas);

        if (!mPath.isEmpty()) {
            int layerCount = canvas.saveLayer(mFrame, null, Canvas.ALL_SAVE_FLAG);
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            boolean isDrawMosaic = false;
            for (IMGPath path : mPath){
                if (path.getMode() == IMGMode.MOSAIC){
                    isDrawMosaic = true;
                    path.onDrawMosaic(canvas, mPaint,scaleCap);
                }
            }
            canvas.restore();
            if (null != mMosaicImage && !mMosaicImage.isRecycled() && isDrawMosaic){
                canvas.drawBitmap(mMosaicImage, null, mFrame, mMosaicPaint);
            }
            canvas.restoreToCount(layerCount);
        }
        // 裁剪
        doClip(canvas);

    }
    private void onDrawImages(Canvas canvas) {
        if (mImage == null || mImage.isRecycled()) {
            return;
        }
        canvas.save();
        RectF clipFrame = getClipFrame();
        canvas.rotate(getRotate(), clipFrame.centerX(), clipFrame.centerY());
        //画图片
        onDrawPicture(canvas);

        for (IMGPath path : mPath){
            if (path.getMode() == IMGMode.DOODLE){
                onDrawDrawDoodles(canvas,path);
            }else if (path.getMode() == IMGMode.CIRCLE){
                onDrawCircles(canvas,path);
            }else if (path.getMode() == IMGMode.ARROW){
                onDrawArrow(canvas,path);
            }else if (path.getMode() == IMGMode.MOSAIC){
                onDrawMosaic(canvas,path);
            }
        }
        // 马赛克
        onDrawMosaic(canvas);
        // 涂鸦
        onDrawDrawDoodles(canvas);
        //画圆
        onDrawCircles(canvas);
        //画箭头
        onDrawArrow(canvas);


        // 文字贴片
        doAddText(canvas);
        // 裁剪
        doClip(canvas);
    }

    @Override
    protected void onCacheDraw(IMGMode mode ,IMGPath path) {
//        if (path.getMode() == IMGMode.DOODLE){
//            onDrawDrawDoodles(cacheCanvas,path);
//        }else if (path.getMode() == IMGMode.CIRCLE){
//            onDrawCircles(cacheCanvas,path);
//        }else if (path.getMode() == IMGMode.ARROW){
//            onDrawArrow(cacheCanvas,path);
//        }else if (path.getMode() == IMGMode.MOSAIC){
//            onDrawMosaic(cacheCanvas,path);
//        }
    }

    private void doClip(Canvas canvas) {
        if (getMode() == IMGMode.CLIP) {
            canvas.save();
            canvas.translate(getScrollX(), getScrollY());
            onDrawClip(canvas, getScrollX(), getScrollY());
            canvas.restore();
        }
    }

    private void doAddText(Canvas canvas) {
        if (isFreezing()) onDrawStickers(canvas);
        onDrawShade(canvas);
        canvas.restore();
        if (!isFreezing()) {
            onDrawStickerClip(canvas);
            onDrawStickers(canvas);
        }
    }

    private void onDrawPicture(Canvas canvas){
        // 裁剪区域
        canvas.clipRect(mClipWin.isClipping() ? mFrame : mClipFrame);
        // 绘制图片
        canvas.drawBitmap(mImage, null, mFrame, null);
        if (DEBUG) {
            // Clip 区域
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(6);
            canvas.drawRect(mFrame, mPaint);
            canvas.drawRect(mClipFrame, mPaint);
        }
    }

    public int onDrawMosaicsPath(Canvas canvas) {
        int layerCount = canvas.saveLayer(mFrame, null, Canvas.ALL_SAVE_FLAG);
        if (!isMosaicEmpty()) {
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            for (IMGPath path : mMosaics) {
                path.onDrawMosaic(canvas, mPaint,scaleCap);
            }
            canvas.restore();
        }

        return layerCount;
    }

    private void onDrawMosaic(Canvas canvas,IMGPath path){
        int layerCount = canvas.saveLayer(mFrame, null, Canvas.ALL_SAVE_FLAG);
        canvas.save();
        float scale = getScale();
        canvas.translate(mFrame.left, mFrame.top);
        canvas.scale(scale, scale);
        path.onDrawMosaic(canvas, mPaint,scaleCap);
        canvas.restore();
        canvas.drawBitmap(mMosaicImage, null, mFrame, mMosaicPaint);
        canvas.restoreToCount(layerCount);
    }

    private void onDrawMosaic(Canvas canvas) {
        // 马赛克
        if (!isMosaicEmpty() || (getMode() == IMGMode.MOSAIC && !overlayPath.isEmpty())) {
            int count = onDrawMosaicsPath(canvas);
            if (getMode() == IMGMode.MOSAIC && !overlayPath.isEmpty()) {
                mDoodlePaint.setStrokeWidth(overlayPath.getRealPaintSize());
                doPaintCommonStyle(mDoodlePaint,overlayPath.getRealPaintSize());
                canvas.save();
                doCanvasTranslate(canvas);
                canvas.drawPath(overlayPath.getPath(), mDoodlePaint);
                canvas.restore();
            }
            canvas.drawBitmap(mMosaicImage, null, mFrame, mMosaicPaint);
            canvas.restoreToCount(count);
        }
    }

    private void onDrawDrawDoodles(Canvas canvas,IMGPath path){
        canvas.save();
        float scale = getScale();
        canvas.translate(mFrame.left, mFrame.top);
        canvas.scale(scale, scale);
        path.onDrawDoodle(canvas, mPaint,scaleCap);
        canvas.restore();
    }

    private void onDrawDrawDoodles(Canvas canvas) {

        if (!isDoodleEmpty()) {
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            for (IMGPath path : mDoodles) path.onDrawDoodle(canvas, mPaint,scaleCap);
            canvas.restore();
        }

        if (getMode() == IMGMode.DOODLE && !overlayPath.isEmpty()) {
            doPaintCommonStyle(mDoodlePaint,overlayPath.getRealPaintSize());
            canvas.save();
            doCanvasTranslate(canvas);
            canvas.drawPath(overlayPath.getPath(), mDoodlePaint);
            canvas.restore();
        }
    }

    private void onDrawCircles(Canvas canvas,IMGPath path){
        canvas.save();
        float scale = getScale();
        canvas.translate(mFrame.left, mFrame.top);
        canvas.scale(scale, scale);
        path.onDrawCircle(canvas, mPaint,scaleCap);
        canvas.restore();
    }

    private void onDrawCircles(Canvas canvas) {

        if (!isCircleEmpty()) {
            canvas.save();
            float scale= getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale,scale);


            for (IMGPath path : mCircle) {
                path.onDrawCircle(canvas, mPaint,scaleCap);
            }
            canvas.restore();
        }


        if (getMode() == IMGMode.CIRCLE && !overlayPath.isEmpty()) {
            doPaintCommonStyle(mDoodlePaint,IMGPath.BASE_PAINT_WIDTH);
            canvas.save();
            doCanvasTranslate(canvas);
            canvas.drawPath(overlayPath.getPath(), mDoodlePaint);
            canvas.restore();
        }
    }

    private void onDrawArrow(Canvas canvas,IMGPath path){
        canvas.save();
        float scale = getScale();
        canvas.translate(mFrame.left, mFrame.top);
        canvas.scale(scale, scale);
        path.onDrawArrow(canvas, mPaint,scaleCap);
        canvas.restore();
    }

    private void onDrawArrow(Canvas canvas) {
        if (!isArrowEmpty()) {
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            for (IMGPath path : mArrow) {
                path.onDrawArrow(canvas, mPaint,scaleCap);
            }
            canvas.restore();
        }

        if (getMode() == IMGMode.ARROW && !overlayPath.isEmpty()) {
            mArrowPaint.setColor(overlayPath.getColor());
            canvas.save();
            doCanvasTranslate(canvas);
            canvas.drawPath(overlayPath.getPath(), mArrowPaint);
            canvas.restore();
        }
    }

    private void doPaintCommonStyle(Paint paint,float width){
        paint.setColor(overlayPath.getColor());
        paint.setStrokeWidth(width);
    }

    private void doCanvasTranslate(Canvas canvas){
        RectF frame = getClipFrame();
        canvas.rotate(-getRotate(), frame.centerX(), frame.centerY());
        canvas.translate(getScrollX(), getScrollY());
    }

    public void onDrawStickerClip(Canvas canvas) {
        M.setRotate(getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.mapRect(mTempClipFrame, mClipWin.isClipping() ? mFrame : mClipFrame);
        canvas.clipRect(mTempClipFrame);
    }

    public void onDrawStickers(Canvas canvas) {
        if (mBackStickers.isEmpty()) return;
        canvas.save();
        for (IMGSticker sticker : mBackStickers) {
            if (!sticker.isShowing()) {
                float tPivotX = sticker.getX() + sticker.getPivotX();
                float tPivotY = sticker.getY() + sticker.getPivotY();

                canvas.save();
                M.setTranslate(sticker.getX(), sticker.getY());
                M.postScale(sticker.getScale(), sticker.getScale(), tPivotX, tPivotY);
                M.postRotate(sticker.getRotation(), tPivotX, tPivotY);

                canvas.concat(M);
                sticker.onSticker(canvas);
                canvas.restore();
            }
        }
        canvas.restore();
    }

    public void onDrawShade(Canvas canvas) {
        if (mMode == IMGMode.CLIP && isSteady) {
            mShade.reset();
            mShade.addRect(mFrame.left - 2, mFrame.top - 2, mFrame.right + 2, mFrame.bottom + 2, Path.Direction.CW);
            mShade.addRect(mClipFrame, Path.Direction.CCW);
            canvas.drawPath(mShade, mShadePaint);
        }
    }

    public void onDrawClip(Canvas canvas, float scrollX, float scrollY) {
        if (mMode == IMGMode.CLIP) {
            mClipWin.onDraw(canvas);
        }
    }

    public void toBackupClip() {
        M.setScale(getScale(), getScale());
        M.postTranslate(mFrame.left, mFrame.top);
        M.mapRect(mClipFrame, mBackupClipFrame);
        setTargetRotate(mBackupClipRotate);
        isRequestToBaseFitting = true;
    }

    @Override
    protected void clearMosaicImage() {
        // 清空马赛克图层
        if (mMosaicImage != null) {
            mMosaicImage.recycle();
        }
        this.mMosaicImage = null;

        makeMosaicBitmap();
    }

    public void onRemoveSticker(IMGSticker sticker) {
        if (mForeSticker == sticker) {
            mForeSticker = null;
        } else {
            mBackStickers.remove(sticker);
        }
    }

    public <S extends IMGSticker> void addSticker(S sticker) {
        if (sticker != null) {
            moveToForeground(sticker);
        }
    }

    public <V extends View & IMGSticker> void addStickerView(V stickerView, FrameLayout.LayoutParams params) {

        addView(stickerView, params);
        stickerView.registerCallback(this);
        addSticker(stickerView);

    }

    public void addStickerText(IMGText text) {
        IMGStickerTextView textView = new IMGStickerTextView(getContext());
        textView.setImageRect(getFrame());
        textView.setText(text);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        // Center of the drawing window.
        layoutParams.gravity = Gravity.CENTER;

        textView.setX(getScrollX());
        textView.setY(getScrollY());

        addStickerView(textView, layoutParams);
        stepRecords.addRecodeToBackStack(new IMGPath(null,IMGMode.TEXT));
        stickerRecords.addRecodeToBackStack(textView);

        IMGSticker sticker;
        while (null != (sticker = stickerRecords.getForwardStackElement())){
            onRemove((IMGStickerView)sticker);
        }
        stickerRecords.clearForwardStack();
    }

    @Override
    public <V extends View & IMGSticker> void onDismiss(V stickerView) {
        moveToBackground(stickerView);
        invalidate();
    }

    @Override
    public <V extends View & IMGSticker> void onShowing(V stickerView) {
        if (mForeSticker != stickerView) {
            moveToForeground(stickerView);
        }
        invalidate();
    }

    @Override
    public <V extends View & IMGSticker> boolean onRemove(V stickerView) {

        onRemoveSticker(stickerView);
        stickerView.unregisterCallback(this);
        ViewParent parent = stickerView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(stickerView);
        }
        return true;
    }


    public boolean isMosaicEmpty() {
        return mMosaics.isEmpty();
    }

    public boolean isDoodleEmpty() {
        return mDoodles.isEmpty();
    }

    public boolean isCircleEmpty() {
        return mCircle.isEmpty();
    }
    public boolean isArrowEmpty() {
        return mArrow.isEmpty();
    }

    public Bitmap saveBitmap(boolean isOperateMosaicOrClip) {
        stickAll();

        float scale = 1f / getScale();
        RectF frame = new RectF(getClipFrame());

        // 旋转基画布
        Matrix m = new Matrix();
        m.setRotate(getRotate(), frame.centerX(), frame.centerY());
        m.mapRect(frame);
        // 缩放基画布
        m.setScale(scale, scale, frame.left, frame.top);
        m.mapRect(frame);
        Bitmap bitmap = Bitmap.createBitmap(Math.round(frame.width()), Math.round(frame.height()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 平移到基画布原点&缩放到原尺寸
        canvas.translate(-frame.left, -frame.top);
        canvas.scale(scale, scale, frame.left, frame.top);

        if (isOperateMosaicOrClip){
            onDrawExtImages(canvas);
        }else {
            onDrawImages(canvas);
        }
        return bitmap;
    }

    public void setPaintRatio(float ratio) {
        overlayPath.setPaintSizeRate(ratio);
    }


    public void stepBack(){
        IMGPath path = stepRecords.popBackStack();
        if (path == null)return;
        IMGMode mode = path.getMode();

        if (mode == IMGMode.TEXT){
            IMGSticker sticker = stickerRecords.popBackStack();

            if (null == mBackStickers || null == sticker)return;
            sticker.dismiss();
             if (!mBackStickers.isEmpty()){
                mBackStickers.remove(mBackStickers.size() - 1);
            }else {
                return;
            }
            invalidate();
            return;
        }
//        List<IMGPath> paths = getPaths(mode);
        if (null != mPath && !mPath.isEmpty()){
            mPath.remove(mPath.size() - 1);
        }
        invalidate();
    }


    public void stepForward(){
        IMGPath path = stepRecords.popForwardStack();
        if (path == null)return;
        IMGMode mode = path.getMode();
        if (mode == IMGMode.TEXT){
            IMGSticker sticker = stickerRecords.popForwardStack();
            if (null != sticker && null != mBackStickers){
                sticker.show();
                mBackStickers.add(sticker);
            }
            invalidate();
            return;
        }

        List<IMGPath> paths = getPaths(mode);
        if (null != paths){
            paths.add(path);
        }
        invalidate();

    }

    private List<IMGPath> getPaths(IMGMode mode){
        List<IMGPath> paths;
        if (mode == IMGMode.DOODLE){
            paths = mDoodles;
        }else if (mode == IMGMode.CIRCLE){
            paths = mCircle;
        }else if (mode == IMGMode.ARROW){
            paths = mArrow;
        }else if (mode == IMGMode.MOSAIC){
            paths = mMosaics;
        }else {
            paths = null;
        }
        return paths;
    }

    public boolean isEditor() {
        if (mPath.isEmpty() && mBackStickers.isEmpty() && null == mForeSticker){
            return false;
        }else {
            for (IMGPath path : mPath){
                if (path.getMode() != IMGMode.MOSAIC && path.getMode() != IMGMode.NONE){
                    return true;
                }
            }
            return !mBackStickers.isEmpty() || null != mForeSticker;
        }
    }

    public boolean isEditorMosaic() {
        for (IMGPath path : mPath){
            if (path.getMode() == IMGMode.MOSAIC){
                return true;
            }
        }
        return false;
    }
}
