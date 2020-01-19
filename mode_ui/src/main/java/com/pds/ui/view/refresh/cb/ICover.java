package com.pds.ui.view.refresh.cb;

import android.view.animation.Transformation;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 16:16
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public interface ICover {
    float totalDragDistance();
    int viewDiameter();
    int currentTargetOffsetTop();
    int targetY();
    void applyTransformation(float interpolatedTime, Transformation t);
}
