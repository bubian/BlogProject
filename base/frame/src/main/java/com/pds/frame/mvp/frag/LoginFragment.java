package com.pds.frame.mvp.frag;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pds.frame.mvp.base.MvpFragment;
import com.pds.frame.mvp.presenter.CTPresenter;
import com.pds.frame.mvp.view.ICTView;

public class LoginFragment extends MvpFragment<ICTView, CTPresenter> implements ICTView {

    @Override
    public CTPresenter createPresenter() {
        return new CTPresenter();
    }

    @Override
    public ICTView createView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().login("13982000002","jjr123","1688");
    }

    @Override
    public void onResult(String result) {
        Toast.makeText(getActivity(), "登录结果：" + result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getPresenter() != null){
            this.getPresenter().detachView();
        }
    }
}
