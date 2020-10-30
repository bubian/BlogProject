package com.pds.base.act;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 15:22
 * Email：pengdaosong@medlinker.com
 * Description: 顶层Fragment
 */
public class BaseFragment extends Fragment {

    protected View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layoutId() <= 0) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        mView = inflater.inflate(layoutId(), container, false);
        return mView;
    }

    protected int layoutId() {
        return -1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    protected void initView() {}
}
