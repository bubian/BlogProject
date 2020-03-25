package com.pds.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.pds.ui.R;
import com.pds.util.unit.UnitConversionUtils;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/28 11:43
 **/
@Deprecated
public class LineView extends View {
    public LineView(Context context) {
        super(context);
        if (!isInEditMode())
            init();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init();
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            init();
    }

    private void init() {
        setBackgroundResource(R.color.color_9a9aa4);
        setMinimumHeight(UnitConversionUtils.dip2px(getContext(),0.5f));
    }
}
