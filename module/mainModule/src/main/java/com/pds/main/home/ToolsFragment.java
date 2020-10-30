package com.pds.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pds.base.act.BaseFragment;
import com.pds.main.R;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 5:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ToolsFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_home_tools,container,false);
    }
}
