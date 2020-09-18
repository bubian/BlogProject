package com.pds.frame.mvvm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.pds.frame.base.BaseActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 13:14
 * Email：pengdaosong@medlinker.com
 * Description: 使用ViewModel顶层Activity
 */
public class VMActivity<VM extends BaseViewModel> extends BaseActivity {

    protected VM mViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVM();
    }

    private void initVM() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<VM> presenterClassType = (Class<VM>) types[0];
                mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(presenterClassType);
                getLifecycle().addObserver(mViewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
