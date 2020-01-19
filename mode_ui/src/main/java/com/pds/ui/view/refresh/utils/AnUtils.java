package com.pds.ui.view.refresh.utils;

import android.view.animation.Animation;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 19:09
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class AnUtils {

    public static boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }
}
