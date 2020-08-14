package com.pds.blog.autosize;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.internal.CustomAdapt;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class CustomFragment1 extends Fragment implements CustomAdapt {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //由于某些原因, 屏幕旋转后 Fragment 的重建, 会导致框架对 Fragment 的自定义适配参数失去效果
        //所以如果您的 Fragment 允许屏幕旋转, 则请在 onCreateView 手动调用一次 AutoSize.autoConvertDensity()
        //如果您的 Fragment 不允许屏幕旋转, 则可以将下面调用 AutoSize.autoConvertDensity() 的代码删除掉
        AutoSize.autoConvertDensity(getActivity(), 1080, true);
        return createTextView(inflater, "Fragment-1\nView width = 360dp\nTotal width = 1080dp", 0xffff0000);
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 1080;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public static View createTextView(LayoutInflater inflater, String content, int backgroundColor) {
        TextView view = new TextView(inflater.getContext());
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams((AutoSizeUtils.dp2px(inflater.getContext(), 360)),
                        ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        view.setText(content);
        view.setTextColor(0xffffffff);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(30);
        view.setBackgroundColor(backgroundColor);
        return view;
    }
}
