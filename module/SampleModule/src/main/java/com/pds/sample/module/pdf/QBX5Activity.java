package com.pds.sample.module.pdf;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.base.act.BaseActivity;
import com.pds.pdf.core.QbSdkManager;
import com.pds.pdf.download.DownloadListener;
import com.pds.pdf.process.ProgressView;
import com.pds.router.module.BundleKey;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.application.ModuleApplication;
import com.pds.web.X5SDK;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 3:10 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.X5_QB)
public class QBX5Activity extends BaseActivity {
    private String mFileUrl;
    private String mUrl;
    private QbSdkManager mManager;
    private ProgressView mProgressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setQbContentView();
        mFileUrl = getIntent().getStringExtra("param");
        X5SDK.check(ModuleApplication.instance().application());
        mManager = new QbSdkManager();
        initQbCallback();
        setDownloadListener();
        loadPDFromUrl();
    }

    public void loadPDFromUrl(){
        mManager.loadPDFromUrl(this, mFileUrl);
    }

    public QbSdkManager getQbSdkManager(){
        return mManager;
    }

    public void setDownloadListener(){
        mManager.setDownloadListener(new DownloadListener() {
            @Override
            public void onProgress(int i) {
                mProgressView.onProgress(i);
            }

            @Override
            public void onFinishDownload(boolean b) {
                mProgressView.onComplete();
            }

            @Override
            public void onStartDownload() {
                mProgressView.onStartDownload();
            }

            @Override
            public void onFail(String message) {
                mProgressView.onComplete();
            }
        });
    }

    public void setQbContentView() {
        mProgressView = new ProgressView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(com.pds.res.R.dimen.dp_3));
        setContentView(mProgressView, params);
    }

    public void initQbCallback() {
        mManager.setQbCallback(value -> {
            if (needClose(value)) {
                finish();
            }
        });
    }

    public boolean needClose(String s) {
        return "fileReaderClosed".equals(s)
                || "openFileReader open in QB".equals(s)
                || "filepath error".equals(s)
                || "TbsReaderDialogClosed".equals(s)
                || "default browser:".equals(s)
                || "filepath error".equals(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.destroy(this);
    }
}
