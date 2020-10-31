package com.pds.sample.module.pdf;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.base.act.BaseActivity;
import com.pds.log.core.Lg;
import com.pds.router.module.BundleKey;
import com.pds.router.module.SampleGroupRouter;
import com.pds.web.widget.HybridWebView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/20 2:46 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.PDF_JS)
public class PDFWebLoadActivity extends BaseActivity {

    private static final String TAG = "PDFWebLoadActivity";

    private static final String NATIVE = "1";
    private static final String ASSETS = "2";
    private HybridWebView mWebView;

    @Autowired(name = "type")
    String mType;
    @Autowired(name = "path")
    String mPdfJsPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mWebView = new X5WebView(this);
        setContentView(mWebView);
        String url = getIntent().getStringExtra(BundleKey.PARAM);
        Uri uri = Uri.parse(url);
        mType = uri.getQueryParameter("type");
        if (TextUtils.isEmpty(mType)) {
            mWebView.loadUrl(buildUrl(url));
        } else {
            mWebView.loadUrl(buildUrl(url.substring(0, url.lastIndexOf("?"))));
        }

    }

    private String buildUrl(String pdfUrl) {
        Lg.d(TAG, "mType = " + mType + ",mPdfJsPath = " + mPdfJsPath);
        if (ASSETS.equals(mType)) {
            return buildUrlLocal(pdfUrl);
        } else if (NATIVE.equals(mType)) {
            return mPdfJsPath;
        } else {
            return getPdfLink(true,pdfUrl);
        }
    }

    private String buildUrlLocal(String pdfUrl) {
        return getPdfLinkLocal() + "?file=" + pdfUrl;
    }

    /**
     * Android10访问被拒绝
     *
     * @param isOld
     * @return
     */
    private String getPdfLink(boolean isOld, String pdfUrl) {
        String u = isOld ? "http://47.104.91.148/pdfjs-es5/web/viewer.html" : "http://47.104.91.148/pdfjs/web/viewer.html";
        return u + "?file=" + pdfUrl;
    }

    private String getPdfLinkLocal() {
        return "file:///android_asset/pdfJs-es5/web/viewer.html";
    }
}
