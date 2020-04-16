package com.pds.ui.view.refresh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.pds.ui.view.refresh.cb.BaseCover;
import com.pds.ui.view.refresh.cb.ICover;
import com.pds.ui.view.refresh.cb.ISpinnerAction;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-15 16:52
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@SuppressLint("AppCompatCustomView")
public abstract class BaseCoverRefreshView extends ImageView implements BaseCover {

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
    public void finishSpinner(float overScrollTop,float slingshotDist,float totalDragDistance) {

    }

    @Override
    public void moveSpinner(float overScrollTop,float slingshotDist,float totalDragDistance) {

    }

    @Override
    public void reset() {

    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public boolean doComplete(float interpolatedTime, Transformation t) {
        return false;
    }

}
