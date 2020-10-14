package com.pds.ui.container;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/24 4:29 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class BgLinearLayout extends LinearLayout {

    public BgLinearLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BgLinearLayout(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BgLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private ContainerDelegate mContainerDelegate = new ContainerDelegate();

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContainerDelegate.init(context, attrs, defStyleAttr, this);
    }

    public void setBgColor(int bgColor) {
        mContainerDelegate.setBgColor(bgColor, this);
    }

    @Override
    public void draw(Canvas canvas) {
        mContainerDelegate.draw(canvas, this);
        super.draw(canvas);
    }
}
