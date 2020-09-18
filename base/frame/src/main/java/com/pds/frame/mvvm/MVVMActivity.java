package com.pds.frame.mvvm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 13:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public abstract class MVVMActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends VMActivity<VM> {

    protected DB mVDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initDataBinding();
        super.onCreate(savedInstanceState);
    }

    private void initDataBinding() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<DB> presenterClassType = (Class<DB>) types[1];
                if (ViewDataBinding.class != presenterClassType && ViewDataBinding.class.isAssignableFrom(presenterClassType)) {
                    mVDB = DataBindingUtil.setContentView(this, layoutId());
                } else setContentView(layoutId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract int layoutId();
}
