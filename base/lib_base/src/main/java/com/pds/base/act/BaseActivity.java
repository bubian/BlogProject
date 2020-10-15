package com.pds.base.act;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.ui.dialog.LoadingDialog;
import com.pds.util.ui.ImmersiveModeUtil;

/**
 * @author: pengDaoSong
 * CreateTime:  2020-09-16 15:21
 * Description: 自定义顶层Activity基类，尽量不要包含特定业务处理
 */
public class BaseActivity extends AppCompatActivity {

    protected LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configImmersiveMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setContentView(int layoutResID) {
        this.setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    public void showDialogLoading() {
        showDialogLoading(true);
    }

    public void showDialogLoading(boolean cancelable) {
        if (checkDialog(cancelable)) {
            return;
        }
        mLoadingDialog.show(getSupportFragmentManager());
    }

    public void showDialogLoadingDelay() {
        showDialogLoadingDelay(true);
    }

    public void showDialogLoadingDelay(boolean cancelable) {
        if (checkDialog(cancelable)) {
            return;
        }
        mLoadingDialog.showDelay(getSupportFragmentManager());
    }

    private boolean checkDialog(boolean cancelable) {
        if (null == mLoadingDialog) {
            mLoadingDialog = LoadingDialog.getInstance(cancelable);
        }
        if (isDestroyed() || mLoadingDialog.isAdded() || mLoadingDialog.isVisible()) {
            return true;
        }
        return false;
    }

    public void hideDialogLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
            mLoadingDialog = null;
        }
    }

    /**
     * 配置沉浸式模式
     */
    protected void configImmersiveMode() {
        ImmersiveModeUtil.setDefaultImmersiveMode(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }
}
