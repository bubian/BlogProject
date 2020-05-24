package com.pds.ui.view.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug;

import androidx.annotation.IntDef;

import com.pds.ui.view.refresh.BaseSwipeRefreshLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.widget.GridLayout.VERTICAL;
import static android.widget.LinearLayout.HORIZONTAL;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-22 14:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class MeasureLayout extends BaseSwipeRefreshLayout {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    private int mOrientation;

    public MeasureLayout(Context context) {
        super(context);
    }

    public MeasureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {


    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {

    }

}
