package com.pds.kotlin.study.ui.behavior;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import com.pds.kotlin.study.R;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by miguel.hincapie on 20/01/2018
 */

public class MergedAppBarLayout extends AppBarLayout {

    protected Toolbar toolbar;
    protected View background;

    public MergedAppBarLayout(Context context) {
        super(context);
        init();
    }

    public MergedAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.merged_appbar_layout, this);
        //to avoid expose xml attributes to the final programmer user, I added some of them here
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        getContext().setTheme(R.style.AppTheme_AppBarOverlay);

        toolbar = findViewById(R.id.expanded_toolbar);
        background = findViewById(R.id.background);
    }
}