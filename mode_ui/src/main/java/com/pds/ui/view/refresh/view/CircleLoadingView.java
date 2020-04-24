package com.pds.ui.view.refresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.pds.ui.R;

/**
 * 菊花loading
 *
 * @author hmy
 */
public class CircleLoadingView extends ImageView {

    private RotateAnimation mRotateAnimation;

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setImageResource(R.drawable.ic_loading_circle);
    }

    /**
     *
     */
    public void startRotateAnimation() {
        startRotateAnimation(1500);
    }

    /**
     *
     */
    public void startRotateAnimation(long duration) {
        if (mRotateAnimation == null) {
            mRotateAnimation = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
            mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setRotation(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        setAnimation(mRotateAnimation);
        mRotateAnimation.setDuration(duration);
        startAnimation(mRotateAnimation);
    }

    /**
     *
     */
    public void cancelAnimator() {
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }
    }

    /**
     * 改变旋转角度
     *
     * @param progress 0.0f-1.0f
     */
    public void changeRotation(float progress) {
        setRotation(progress * 360);
    }
}
