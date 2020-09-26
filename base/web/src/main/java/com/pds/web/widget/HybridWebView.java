package com.pds.web.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebView;

public class HybridWebView extends WebView {
    public HybridWebView(Context context) {
        super(context);
        init();
    }

    public HybridWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HybridWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        // KITKAT
//        if (Build.VERSION.SDK_INT >= 19) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        boolean canScroll = direction < 0 ? getWebScrollY() > 0 : super.canScrollVertically(direction);
//        Log.w("yjt","HybridWebView canscrollVertically direction = "+direction+ "; canScroll = "+canScroll+" webScrollY = "+getWebScrollY()+"; scrollY 999= "+getScrollY());
        return canScroll;
    }
}
