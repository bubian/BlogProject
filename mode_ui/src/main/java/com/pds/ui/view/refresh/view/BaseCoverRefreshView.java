package com.pds.ui.view.refresh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.ISpinnerAction;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 16:52
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@SuppressLint("AppCompatCustomView")
public class BaseCoverRefreshView extends ImageView implements ISpinnerAction, ICover {
    public BaseCoverRefreshView(Context context) {
        super(context);
    }

    public BaseCoverRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCoverRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public float totalDragDistance() {
        return 0;
    }

    @Override
    public int viewDiameter() {
        return 0;
    }

    @Override
    public int currentTargetOffsetTop() {
        return 0;
    }

    @Override
    public int targetY() {
        return 0;
    }

    @Override
    public void applyTransformation(float interpolatedTime, Transformation t) {

    }

    @Override
    public void finishSpinner(float overScrollTop) {

    }

    @Override
    public void moveSpinner(float overScrollTop) {

    }

    @Override
    public void reset() {

    }
}
