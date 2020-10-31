package com.pds.application;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.pds.base.act.BaseActivity;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.MainGroupRouter;

import java.util.Timer;
import java.util.TimerTask;

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
        mTimer.schedule(mTask,2_000);
    }

    private class SplashTimerTask extends TimerTask {
        @Override
        public void run() {
            mRoot.post(SplashActivity.this::jumpHome);
            mTask = null;
            mTimer.cancel();
        }
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
