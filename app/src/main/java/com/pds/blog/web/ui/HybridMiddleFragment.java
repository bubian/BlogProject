package com.pds.blog.web.ui;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pds.blog.web.common.HybridConstants;
import com.tencent.smtt.sdk.ValueCallback;

import static android.app.Activity.RESULT_OK;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 15:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HybridMiddleFragment extends HybridBaseFragment {
    private static final String TAG = "HbMiddleFragment";

    private static ValueCallback<Uri> mUploadMessage;
    private static ValueCallback<Uri[]> uploadMessageAboveL;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG,"Hb log:onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        if (RESULT_OK == resultCode){
            switch (requestCode){
                case HybridConstants.FILE_CHOOSER_RESULT_CODE:{
                    Uri result = intent == null  ? null : intent.getData();
                    if (uploadMessageAboveL != null) {
                        hbUploadPictures(intent);
                    } else if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(result);
                        mUploadMessage = null;
                    }
                }
                default:{}
            }
        }

    }

    private void hbUploadPictures(Intent intent) {
        if (uploadMessageAboveL == null){
            return;
        }
        Uri[] results = null;
        if (intent != null) {
            String dataString = intent.getDataString();
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }
}
