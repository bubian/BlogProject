package com.pds.ui.view;/**
 * Created by kuiwen on 2015/11/24.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.pds.ui.R;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/11/24 19:43
 **/
public class ThemeProgressBar extends ProgressBar {

    public ThemeProgressBar(Context context) {
        super(context);
        init();
    }

    private boolean isGray;

    public ThemeProgressBar(Context context, boolean gray) {
        super(context);
        isGray = gray;
        init();
    }

    public ThemeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        final Drawable drawable = getResources()
                .getDrawable(isGray ? R.drawable.progressbar_gray : R.drawable.progressbar);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setIndeterminateDrawable(drawable);
    }

}
