package com.pds.edit.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.anim.IMGHomingAnimator;
import com.pds.edit.core.homing.IMGHoming;
import com.pds.edit.core.util.IMGUtils;

public class IMGHomingView extends IMGTranslateView implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener{

    private static final String TAG = "IMGHomingView";

    private IMGHomingAnimator mHomingAnimator;

    private boolean isAnimCanceled = false;
    //是否初始位置
    protected boolean isInitialHoming = false;

    public IMGHomingView(Context context) {
        this(context,null);
    }

    public IMGHomingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IMGHomingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void doRotate() {
        if (!isHoming()) {
            rotate(-90);
            onHoming();
        }
    }


    public void onSteady(float scrollX, float scrollY) {
        isSteady = true;
        onClipHoming();
        mClipWin.setShowShade(true);
    }

    public boolean onClipHoming() {
        return mClipWin.homing();
    }

    /**
     * 裁剪区域旋转回原始角度后形成新的裁剪区域，旋转中心发生变化，
     * 因此需要将视图窗口平移到新的旋转中心位置。
     */

    protected void onImageChanged() {
        isInitialHoming = false;
        onWindowChanged(mWindow.width(), mWindow.height());
        if (mMode == IMGMode.CLIP) {
            mClipWin.reset(mClipFrame, getTargetRotate());
        }
    }

    protected void doHomingRunable(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                removeCallbacks(homingRunnable);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                postDelayed(homingRunnable, 1000);
                break;
        }
    }

    protected Runnable homingRunnable = new Runnable() {
        @Override
        public void run() {
            // 稳定触发
            if (!onSteady()) {
                postDelayed(this, 200);
            }
        }
    };



    boolean onSteady() {
        if (!isHoming()) {
            onSteady(getScrollX(), getScrollY());
            onHoming();
            return true;
        }
        return false;
    }

    @Override
    protected boolean isHoming() {
        return mHomingAnimator != null && mHomingAnimator.isRunning();
    }

    protected void onHoming() {
        invalidate();
        stopHoming();
        startHoming(getStartHoming(getScrollX(), getScrollY()),
                getEndHoming(getScrollX(), getScrollY()));
    }

    private void startHoming(IMGHoming sHoming, IMGHoming eHoming) {
        if (mHomingAnimator == null) {
            mHomingAnimator = new IMGHomingAnimator();
            mHomingAnimator.addUpdateListener(this);
            mHomingAnimator.addListener(this);
        }
        mHomingAnimator.setHomingValues(sHoming, eHoming);
        mHomingAnimator.start();
    }


    public IMGHoming getStartHoming(float scrollX, float scrollY) {
        return new IMGHoming(scrollX, scrollY, getScale(), getRotate());
    }

    public void onHomingStart(boolean isRotate) {
        isAnimCanceled = false;
        isDrawClip = true;
    }

    public void onHoming(float fraction) {
        mClipWin.homing(fraction);
    }

    public boolean onHomingEnd(float scrollX, float scrollY, boolean isRotate) {
        isDrawClip = true;
        if (mMode == IMGMode.CLIP) {
            // 开启裁剪模式

            boolean clip = !isAnimCanceled;

            mClipWin.setHoming(false);
            mClipWin.setClipping(true);
            mClipWin.setResetting(false);

            return clip;
        } else {
            if (isFreezing && !isAnimCanceled) {

                isFreezing = false;
                setFreezing(false);
            }
        }
        return false;
    }

    public void onHomingCancel(boolean isRotate) {
        isAnimCanceled = true;
    }


    public IMGHoming getEndHoming(float scrollX, float scrollY) {
        IMGHoming homing = new IMGHoming(scrollX, scrollY, getScale(), getTargetRotate());

        if (mMode == IMGMode.CLIP) {
            RectF frame = new RectF(mClipWin.getTargetFrame());
            frame.offset(scrollX, scrollY);
            if (mClipWin.isResetting()) {

                RectF clipFrame = new RectF();
                M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                M.mapRect(clipFrame, mClipFrame);

                homing.rConcat(IMGUtils.fill(frame, clipFrame));
            } else {
                RectF cFrame = new RectF();

                // cFrame要是一个暂时clipFrame
                if (mClipWin.isHoming()) {
//
//                    M.mapRect(cFrame, mClipFrame);

//                    mClipWin
                    // TODO 偏移中心

                    M.setRotate(getTargetRotate() - getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                    M.mapRect(cFrame, mClipWin.getOffsetFrame(scrollX, scrollY));

                    homing.rConcat(IMGUtils.fitHoming(frame, cFrame, mClipFrame.centerX(), mClipFrame.centerY()));


                } else {
                    M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                    M.mapRect(cFrame, mFrame);
                    homing.rConcat(IMGUtils.fillHoming(frame, cFrame, mClipFrame.centerX(), mClipFrame.centerY()));
                }

            }
        } else {
            RectF clipFrame = new RectF();
            M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
            M.mapRect(clipFrame, mClipFrame);

            RectF win = new RectF(mWindow);
            win.offset(scrollX, scrollY);
            homing.rConcat(IMGUtils.fitHoming(win, clipFrame, isRequestToBaseFitting));
            isRequestToBaseFitting = false;
        }

        return homing;
    }

    private void onInitialHoming(float width, float height) {
        mFrame.set(0, 0, mImage.getWidth(), mImage.getHeight());
        mClipFrame.set(mFrame);
        mClipWin.setClipWinSize(width, height);

        if (mClipFrame.isEmpty()) {
            return;
        }

        toBaseHoming();

        isInitialHoming = true;
        onInitialHomingDone();
    }

    private void onInitialHomingDone() {
        if (mMode == IMGMode.CLIP) {
            mClipWin.reset(mClipFrame, getTargetRotate());
        }
    }


    private void toBaseHoming() {
        if (mClipFrame.isEmpty()) {
            // Bitmap invalidate.
            return;
        }

        float scale = Math.min(
                mWindow.width() / mClipFrame.width(),
                mWindow.height() / mClipFrame.height()
        );

        // Scale to fit window.
        M.setScale(scale, scale, mClipFrame.centerX(), mClipFrame.centerY());
        M.postTranslate(mWindow.centerX() - mClipFrame.centerX(), mWindow.centerY() - mClipFrame.centerY());
        M.mapRect(mFrame);
        M.mapRect(mClipFrame);
    }

    public IMGHoming clip(float scrollX, float scrollY) {
        RectF frame = mClipWin.getOffsetFrame(scrollX, scrollY);

        M.setRotate(-getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.mapRect(mClipFrame, frame);

        return new IMGHoming(
                scrollX + (mClipFrame.centerX() - frame.centerX()),
                scrollY + (mClipFrame.centerY() - frame.centerY()),
                getScale(), getRotate()
        );
    }

    @Override
    protected void stopHoming() {
        if (mHomingAnimator != null) {
            mHomingAnimator.cancel();
        }
    }

    protected void toApplyHoming(IMGHoming homing) {
        setScale(homing.scale);
        setRotate(homing.rotate);
        if (!onScrollTo(Math.round(homing.x), Math.round(homing.y))) {
            invalidate();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        onHoming(animation.getAnimatedFraction());
        toApplyHoming((IMGHoming) animation.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (DEBUG) {
            Log.d(TAG, "onAnimationStart");
        }
        onHomingStart(mHomingAnimator.isRotate());
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (DEBUG) {
            Log.d(TAG, "onAnimationEnd");
        }
        if (onHomingEnd(getScrollX(), getScrollY(), mHomingAnimator.isRotate())) {
            toApplyHoming(clip(getScrollX(), getScrollY()));
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (DEBUG) {
            Log.d(TAG, "onAnimationCancel");
        }
        onHomingCancel(mHomingAnimator.isRotate());
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // empty implementation.
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(homingRunnable);
        release();
    }


    protected void onWindowChanged(float width, float height) {
        if (width == 0 || height == 0) {
            return;
        }

        mWindow.set(0, 0, width, height);

        if (!isInitialHoming) {
            onInitialHoming(width, height);
        } else {

            // Pivot to fit window.
            M.setTranslate(mWindow.centerX() - mClipFrame.centerX(), mWindow.centerY() - mClipFrame.centerY());
            M.mapRect(mFrame);
            M.mapRect(mClipFrame);
        }

        mClipWin.setClipWinSize(width, height);
    }
}
