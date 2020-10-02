package com.pds.web.x5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 16:32
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class X5WebChromeClient extends WebChromeClient {
    private Activity mActivity;
    private WebChromeClient mChromeClient;

    public X5WebChromeClient(Activity activity) {
        mActivity = activity;
    }

    public void setExtenalWebChromeClient(WebChromeClient webChromeClient) {
        mChromeClient = webChromeClient;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        boolean showProgress = newProgress < 100;
//        mProgessbar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgessbar.setProgress(newProgress);
        if (null != mChromeClient) mChromeClient.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (null != mChromeClient)
            mChromeClient.onReceivedTitle(view, title);
    }

    //For Android 4.1
    @Override
    public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mActivity.runOnUiThread(() -> {
//            mUploadMessage = uploadMsg;
            picker();
        });

    }

    @Override
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        mActivity.runOnUiThread(() -> {
//                uploadMessageAboveL = filePathCallback;
            picker();
        });
        return true;
    }

    private void picker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mActivity.startActivityForResult(intent, 1);
        } else {
            mActivity.startActivityForResult(Intent.createChooser(intent, "图片选择"), 1);
        }
    }
}
