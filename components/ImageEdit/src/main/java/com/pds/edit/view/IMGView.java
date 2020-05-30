package com.pds.edit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.clip.IMGClip;
import com.pds.edit.core.clip.IMGClipWindow;
import com.pds.edit.core.sticker.IMGSticker;

import java.util.ArrayList;
import java.util.List;

// TODO clip外不加入path
public class IMGView extends FrameLayout{

    protected static final boolean DEBUG = false;

    protected Bitmap mImage;
    //完整图片边框
    protected RectF mFrame = new RectF();
    //裁剪图片边框（显示的图片区域）
    protected RectF mClipFrame = new RectF();
    //裁剪模式前状态备份
    protected RectF mBackupClipFrame = new RectF();
    protected float mBackupClipRotate = 0;
    protected float mRotate = 0, mTargetRotate = 0;
    protected boolean isRequestToBaseFitting = false;
    //裁剪模式时当前触摸锚点
    protected IMGClip.Anchor mAnchor;
    protected boolean isSteady = true;
    protected Path mShade = new Path();
    //裁剪窗口
    protected IMGClipWindow mClipWin = new IMGClipWindow();
    protected boolean isDrawClip = false;
    //编辑模式
    protected IMGMode mMode = IMGMode.NONE;
    protected boolean isFreezing = mMode == IMGMode.CLIP;
    //可视区域，无Scroll 偏移区域
    protected RectF mWindow = new RectF();
    //当前选中贴片
    protected IMGSticker mForeSticker;
    //为被选中贴片
    protected List<IMGSticker> mBackStickers = new ArrayList<>();

    protected Matrix M = new Matrix();
    protected static final Bitmap DEFAULT_IMAGE = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

    public IMGView(Context context) {
        this(context, null, 0);
    }

    public IMGView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMGView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImage = DEFAULT_IMAGE;

    }
    public void setImageBitmap(Bitmap image) {
        setBitmap(image);
        invalidate();
    }

    public void stickAll() {
        moveToBackground(mForeSticker);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            onWindowChanged(right - left, bottom - top);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            return onInterceptTouch(ev) || super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    boolean onInterceptTouch(MotionEvent event) {
        if (isHoming()) {
            stopHoming();
            return true;
        } else if (getMode() == IMGMode.CLIP) {
            return true;
        }
        return false;
    }


    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        this.mImage = bitmap;
        clearMosaicImage();
        onImageChanged();
    }

    public IMGMode getMode() {
        return mMode;
    }

    protected void clearMosaicImage(){}

    public RectF getClipFrame() {
        return mClipFrame;
    }

    public RectF getFrame() {
        return mFrame;
    }


    protected void moveToForeground(IMGSticker sticker) {
        if (sticker == null) return;

        moveToBackground(mForeSticker);

        if (sticker.isShowing()) {
            mForeSticker = sticker;
            // 从BackStickers中移除
            mBackStickers.remove(sticker);
        } else sticker.show();
    }

    protected void moveToBackground(IMGSticker sticker) {
        if (sticker == null) return;

        if (!sticker.isShowing()) {
            // 加入BackStickers中
            if (!mBackStickers.contains(sticker)) {
                mBackStickers.add(sticker);
            }

            if (mForeSticker == sticker) {
                mForeSticker = null;
            }
        } else sticker.dismiss();
    }

    public void onTouchDown(float x, float y) {
        isSteady = false;
        moveToBackground(mForeSticker);
        if (mMode == IMGMode.CLIP) {
            mAnchor = mClipWin.getAnchor(x, y);
        }
    }

    public void onTouchUp(float scrollX, float scrollY) {
        if (mAnchor != null) {
            mAnchor = null;
        }
    }

    public void release() {
        if (mImage != null && !mImage.isRecycled()) {
            mImage.recycle();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (DEFAULT_IMAGE != null) {
            DEFAULT_IMAGE.recycle();
        }
    }


    protected void onWindowChanged(float width, float height){}
    protected void onImageChanged(){}
    protected void stopHoming(){}
    protected boolean isHoming(){return false;}


}
