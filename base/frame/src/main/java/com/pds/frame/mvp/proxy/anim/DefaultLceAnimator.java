package com.pds.frame.mvp.proxy.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.view.View;

import com.pds.frame.R;

/**
 * 作者: Dream on 2017/8/30 21:33
 * QQ:510278658
 * E-mail:510278658@qq.com
 */

public class DefaultLceAnimator implements ILceAnimator {

    @Override
    public void showLoadingView(View loadingView, View contentView, View errorView) {
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContentView(final View loadingView,final View contentView,final View errorView) {
        if (contentView.getVisibility() == View.VISIBLE) {
            // No Changing needed, because contentView is already visible
            errorView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
        } else {

            errorView.setVisibility(View.GONE);

            final Resources resources = loadingView.getResources();
            final int translateInPixels = 200;
            // Not visible yet, so animate the view in
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator contentFadeIn = ObjectAnimator.ofFloat(contentView,
                    "alpha", 0f, 1f);
            ObjectAnimator contentTranslateIn = ObjectAnimator.ofFloat(
                    contentView, "translationY", translateInPixels, 0);

            ObjectAnimator loadingFadeOut = ObjectAnimator.ofFloat(loadingView,
                    "alpha", 1f, 0f);
            ObjectAnimator loadingTranslateOut = ObjectAnimator.ofFloat(
                    loadingView, "translationY", 0, -translateInPixels);

            set.playTogether(contentFadeIn, contentTranslateIn, loadingFadeOut,
                    loadingTranslateOut);
            set.setDuration(500);

            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    contentView.setTranslationY(0);
                    loadingView.setTranslationY(0);
                    contentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loadingView.setVisibility(View.GONE);
                    loadingView.setAlpha(1f); // For future showLoading calls
                    contentView.setTranslationY(0);
                    loadingView.setTranslationY(0);
                }
            });

            set.start();
        }
    }

    @Override
    public void showErrorView(final View loadingView,final View contentView,final View errorView) {
        contentView.setVisibility(View.GONE);

        final Resources resources = loadingView.getResources();
        // Not visible yet, so animate the view in
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator in = ObjectAnimator.ofFloat(errorView, "alpha", 1f);
        ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView,
                "alpha", 0f);

        set.playTogether(in, loadingOut);
        set.setDuration(600);

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                errorView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loadingView.setVisibility(View.GONE);
                loadingView.setAlpha(1f); // For future showLoading calls
            }
        });

        set.start();
    }

}
