package com.pds.sample.module.pdf;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.base.act.BaseActivity;
import com.pds.pdf.core.X5PDFView;
import com.pds.router.module.BundleKey;
import com.pds.router.module.SampleGroupRouter;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 2:03 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.FIlE_X5_LOAD)
public class PDFX5Activity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayFromX5(getIntent().getStringExtra(BundleKey.PARAM));
    }

    private X5PDFView mX5PDFView;

    private void displayFromX5(String url) {
        mX5PDFView = new X5PDFView(this);
        setContentView(mX5PDFView);
        mX5PDFView.useDefaultProgressView().loadPDFromUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mX5PDFView) {
            mX5PDFView.destroy();
        }
    }
}
