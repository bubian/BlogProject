package com.pds.base.act;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.pds.base.R;
import com.pds.ui.view.NavigationBar;
import com.pds.util.unit.UnitConversionUtils;


/**
 * @author hmy
 * @time 2020/9/22 11:30
 */
public class BaseCompatActivity extends BaseActivity {
    protected NavigationBar mNavigationBar;
    private View mContent;
    private View mShadowView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationBar = new NavigationBar(this);
        mNavigationBar.setId(R.id.base_navigation_bar);
        mNavigationBar.setLeftIcon(R.mipmap.icon_naviback_gray);
        mNavigationBar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initActionBar(mNavigationBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 是否注册EventBus，默认不注册
     *
     * @return
     */
    protected Boolean registerEventBus() {
        return false;
    }


    protected boolean needShadow() {
        return true;
    }

    protected boolean isShowNavigationBar() {
        return true;
    }

    @Override
    public void setContentView(View view) {
        if (isShowNavigationBar()) {
            if (needShadow()) {
                mContent = view;
                FrameLayout mFrameLayout = new FrameLayout(this);
                mFrameLayout.addView(this.mNavigationBar, FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                params.topMargin = UnitConversionUtils.dip2px(this, 48);
                mFrameLayout.addView(mContent, params);

                mShadowView = new View(this);
                mShadowView.setBackgroundResource(R.mipmap.bg_shadow);
                FrameLayout.LayoutParams shadowParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        UnitConversionUtils.dip2px(this, 5));
                shadowParams.topMargin = UnitConversionUtils.dip2px(this, 48);
                mFrameLayout.addView(mShadowView, shadowParams);
                super.setContentView(mFrameLayout);
            } else {
                mContent = view;
                FrameLayout mFrameLayout = new FrameLayout(this);
                mFrameLayout.addView(this.mNavigationBar, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.topMargin = UnitConversionUtils.dip2px(this, 48);
                mFrameLayout.addView(mContent, params);
                super.setContentView(mFrameLayout);
            }
        } else {
            super.setContentView(view);
        }
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    protected void initActionBar(NavigationBar navigationBar) {
    }

    public void hideNavigationBar() {
        if (mNavigationBar.getVisibility() != View.GONE) {
            mNavigationBar.setVisibility(View.GONE);
            if (mShadowView != null) {
                mShadowView.setVisibility(View.GONE);
            }
            if (mContent != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                layoutParams.topMargin = 0;
                mContent.setLayoutParams(layoutParams);
            }
        }
    }

    public void showNavigationBar() {
        if (mNavigationBar.getVisibility() != View.VISIBLE) {
            mNavigationBar.setVisibility(View.VISIBLE);
            mShadowView.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
            layoutParams.topMargin = UnitConversionUtils.dip2px(this, 48);
            mContent.setLayoutParams(layoutParams);
        }
    }

}
