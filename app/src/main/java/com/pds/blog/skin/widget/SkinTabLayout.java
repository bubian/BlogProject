package com.pds.blog.skin.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.google.android.material.tabs.TabLayout;
import com.pds.blog.R;
import com.pds.skin.SkinViewSupport;
import com.pds.skin.utils.SkinResources;


public class SkinTabLayout extends TabLayout implements SkinViewSupport {
    int tabIndicatorColorResId;
    int tabTextColorResId;

    public SkinTabLayout(Context context) {
        this(context, null, 0);
    }

    public SkinTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabLayout,
                defStyleAttr, 0);
        tabIndicatorColorResId = a.getResourceId(R.styleable.TabLayout_tabIndicatorColor, 0);
        tabTextColorResId = a.getResourceId(R.styleable.TabLayout_tabTextColor, 0);
        a.recycle();
    }



    @Override
    public void applySkin() {
        if (tabIndicatorColorResId != 0) {
            int tabIndicatorColor = SkinResources.getInstance().getColor(tabIndicatorColorResId);
            setSelectedTabIndicatorColor(tabIndicatorColor);
        }

        if (tabTextColorResId != 0) {
            ColorStateList tabTextColor = SkinResources.getInstance().getColorStateList
                    (tabTextColorResId);
            setTabTextColors(tabTextColor);
        }
    }

}
