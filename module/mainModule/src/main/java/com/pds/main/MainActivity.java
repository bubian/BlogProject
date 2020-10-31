package com.pds.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pds.base.act.BaseActivity;
import com.pds.main.home.AndroidFragment;
import com.pds.main.home.FlutterFragment;
import com.pds.main.home.ReactFragment;
import com.pds.main.home.ToolsFragment;
import com.pds.router.module.MainGroupRouter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/16 4:47 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

@Route(path = MainGroupRouter.HOME)
public class MainActivity extends BaseActivity {

    @BindView(R2.id.vp2)
    ViewPager2 mVp2;
    @BindView(R2.id.bnv)
    BottomNavigationView mBnv;

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
                switch (position){
                    case 0:
                        return new AndroidFragment();
                    case 1:
                        return new FlutterFragment();
                    case 2:
                        return new ReactFragment();
                    case 3:
                        return new ToolsFragment();
                    default:return null;
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
                if(!menuItem.isChecked()){
                    mBnv.getMenu().getItem(position).setChecked(true);
                }
            }
        });
    }

    private void initNav() {
        mBnv.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page1) {
                mVp2.setCurrentItem(0);
            } else if (itemId == R.id.page2) {
                mVp2.setCurrentItem(1);
            } else if (itemId == R.id.page3) {
                mVp2.setCurrentItem(2);
            } else if (itemId == R.id.page4) {
                mVp2.setCurrentItem(3);
            }
            // 返回true点击tab时候会闪烁
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
