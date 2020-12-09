package com.pds.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pds.base.act.BaseActivity;
import com.pds.flutter.FFragment;
import com.pds.main.home.AndroidFragment;
import com.pds.main.home.ToolsFragment;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.BundleKey;
import com.pds.router.module.MainGroupRouter;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.util.ui.ImmersiveModeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/16 4:47 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

@Route(path = MainGroupRouter.HOME)
public class MainActivity extends BaseActivity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    @BindView(R2.id.vp2)
    ViewPager2 mVp2;
    @BindView(R2.id.bnv)
    BottomNavigationView mBnv;
    private FFragment mFFragment;

    private static final String ROOT_ROUTE = "ReactNativeApp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNav();
        initVP();
    }

    private void initVP() {
        mVp2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new AndroidFragment();
                    case 1:
                        return mFFragment = FFragment.createDefault();
                    case 2:
                        return ARouterHelper.navFrag(ModuleGroupRouter.RN_FRAGMENT, BundleKey.URL, ROOT_ROUTE);
                    case 3:
                        return new ToolsFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        mVp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MenuItem menuItem = mBnv.getMenu().getItem(position);
                if (!menuItem.isChecked()) {
                    mBnv.getMenu().getItem(position).setChecked(true);
                }
                if (R.id.page2 == menuItem.getItemId()) {
                    mVp2.postDelayed(() -> ImmersiveModeUtil.setStatusBarDarkMode(MainActivity.this, true), 2_000);
                }
            }
        });
    }

    private void initNav() {
        mBnv.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page1) {
                mVp2.setCurrentItem(0, false);
            } else if (itemId == R.id.page2) {
                mVp2.setCurrentItem(1, false);
            } else if (itemId == R.id.page3) {
                mVp2.setCurrentItem(2, false);
            } else if (itemId == R.id.page4) {
                mVp2.setCurrentItem(3, false);
            }
            // 返回true点击tab时候会闪烁
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (null != mFFragment){
            mFFragment.onBackPressed();
            return;
        }
        System.exit(0);
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }
}
