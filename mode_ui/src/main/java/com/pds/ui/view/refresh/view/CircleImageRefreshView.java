package com.pds.ui.view.refresh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.pds.ui.view.refresh.utils.AnUtils;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 17:51
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@SuppressLint("AppCompatCustomView")
public class CircleImageRefreshView extends BaseCoverRefreshView {
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private static final int MAX_ALPHA = 255;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final float MAX_PROGRESS_ANGLE = .8f;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;
    private int mCircleDiameter;
    private CircularProgressDrawable mProgress;

    private Animation.AnimationListener mListener;
    int mShadowRadius;
    boolean mScale;

    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    private boolean mRefreshing;

    public CircleImageRefreshView(Context context) {
        this(context,CIRCLE_BG_LIGHT);
        mProgress = new CircularProgressDrawable(getContext());
        mProgress.setStyle(CircularProgressDrawable.DEFAULT);
        setImageDrawable(mProgress);
        setVisibility(View.GONE);
    }

    public CircleImageRefreshView(Context context, int color) {
        super(context);
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new CircleImageRefreshView.OvalShadow(mShadowRadius);
            circle = new ShapeDrawable(oval);
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(color);
        ViewCompat.setBackground(this, circle);
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        if (!elevationSupported()) {
            setMeasuredDimension(mCircleDiameter + mShadowRadius * 2, mCircleDiameter
                    + mShadowRadius * 2);
        }else {
            setMeasuredDimension(mCircleDiameter, mCircleDiameter);
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;

        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = CircleImageRefreshView.this.getWidth();
            final int viewHeight = CircleImageRefreshView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2,
                    mShadowRadius, new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT },
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }

    private Animation mScaleDownAnimation;
    void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        setAnimationListener(listener);
        clearAnimation();
        startAnimation(mScaleDownAnimation);
    }

    @Override
    public void applyTransformation(float interpolatedTime, Transformation t) {
        if (!mScale){
            setAnimationProgress(1 - interpolatedTime);
        }
    }

    @Override
    public void finishSpinner(float overScrollTop,float slingshotDist,float totalDragDistance) {

        Animation.AnimationListener listener = null;
        if (!mScale) {
            listener = new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mRefreshing) {
                        // Make sure the progress view is fully visible
                        mProgress.setAlpha(MAX_ALPHA);
                        mProgress.start();
                    } else {
                        reset();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            };
        }

        if (overScrollTop > totalDragDistance){
            if (mRefreshing) {
                animateOffsetToCorrectPosition(listener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
            return;
        }

        mProgress.setStartEndTrim(0f, 0f);

        animateOffsetToCorrectPosition(listener);
        mProgress.setArrowEnabled(false);
    }
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
    private void animateOffsetToCorrectPosition(Animation.AnimationListener listener) {
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            setAnimationListener(listener);
        }
        clearAnimation();
        startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
//                mCurrentTargetOffsetTop = mCircleView.getTop();
            } else {
                reset();
            }
        }
    };

    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;
    private float dragPercent;
    private float tensionPercent;

    @Override
    public void moveSpinner(float overScrollTop,float slingshotDist,float totalDragDistance) {
        mProgress.setArrowEnabled(true);
        if (!mScale) {
            setScaleX(1f);
            setScaleY(1f);
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overScrollTop / totalDragDistance));
        }

        if (overScrollTop < totalDragDistance) {
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                    && !AnUtils.isAnimationRunning(mAlphaStartAnimation)) {
                startProgressAlphaStartAnimation();
            }
        } else {
            if (mProgress.getAlpha() < MAX_ALPHA && !AnUtils.isAnimationRunning(mAlphaMaxAnimation)) {
                startProgressAlphaMaxAnimation();
            }
        }

        float originalDragPercent = overScrollTop / totalDragDistance;
        dragPercent = Math.min(1f, Math.abs(originalDragPercent));

        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float strokeStart = adjustedPercent * .8f;
        float extraOS = Math.abs(overScrollTop) - totalDragDistance;

        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;

        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);
    }

    @Override
    public void setRefreshState(boolean isRefreshing) {
        mRefreshing = isRefreshing;
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha(
                        (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        setAnimationListener(null);
        clearAnimation();
        startAnimation(alpha);
        return alpha;
    }

    void setAnimationProgress(float progress) {
        setScaleX(progress);
        setScaleY(progress);
    }

    @Override
    public void reset() {

    }


    @Override
    public int viewDiameter() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        return mCircleDiameter;
    }
}
