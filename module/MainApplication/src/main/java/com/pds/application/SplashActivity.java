package com.pds.application;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blog.pds.net.SchedulersCompat;
import com.pds.base.act.BaseActivity;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.MainGroupRouter;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/29 7:28 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class SplashActivity extends BaseActivity {
    private View mRoot;

    private SplashTimerTask mTask;
    private final Timer mTimer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoot = getLayoutInflater().inflate(R.layout.activity_splash,null);
        setContentView(mRoot);
        mTask = new SplashTimerTask();
        initPermissions();
    }

    private class SplashTimerTask extends TimerTask {
        @Override
        public void run() {
            mRoot.post(SplashActivity.this::jumpHome);
            mTask = null;
            mTimer.cancel();
        }
    }

    private void initPermissions() {
        // 适配权限检测，读取权限
        Disposable disposable = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .compose(SchedulersCompat.applyNewSchedulers())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mTimer.schedule(mTask,1_000);
                    }
                }, Throwable::printStackTrace);
    }

    private void jumpHome(){
        ARouterHelper.nav(this, MainGroupRouter.HOME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().getDecorView().postDelayed(this::finish,2_000);
    }
}
