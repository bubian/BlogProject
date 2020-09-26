package com.pds.web.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.web.core.HybridConstant;
import com.pds.web.param.HybridParamCallback;
import com.pds.web.param.HybridParamLog;
import com.pds.web.param.HybridParamShowLoading;
import com.pds.web.param.HybridParamUpload;
import com.pds.web.router.ModuleHybridManager;
import com.pds.web.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

@Route(path = ModuleHybridManager.ROUTE_HYBRID)
public class HybridWebViewActivity extends AppCompatActivity {

    // 选择图片
    public static final int FILECHOOSER_RESULTCODE = 1;
    //图片上传
    private final static int SELECT_PIC_REQUEST_CODE = 2;
    private final static int LOGIN_REQUEST_CODE = 3;

    private static final String TAG = HybridWebViewActivity.class.getSimpleName();

    private HybridParamUpload mHybridParamUpload;
    private HybridParamLog mHybridLoginParam;
    private String url = "";
    HybridBaseFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //X5兼容网页视频闪烁情况
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//（这个对宿主没什么影响，建议声明）
        ModuleHybridManager.getInstance().getService().configImmersiveMode(this);
        try {
            Intent intent = getIntent();
            if (null == intent) {
                finish();
                return;
            }
            boolean isNormal = intent
                    .getBooleanExtra(HybridConstant.INTENT_EXTRA_KEY_ISNORMAL, false);
            url = intent.getStringExtra(HybridConstant.INTENT_EXTRA_KEY_TOPAGE);
            fragment = createHybridFragment(isNormal);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment,
                    HybridWebViewFragment.class.getSimpleName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected HybridBaseFragment createHybridFragment(boolean isNormal) {
        return isNormal ? HybridNormalWebViewFragment.newInstance(url)
                : HybridWebViewFragment.newInstance(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LogUtil.i(TAG, String.format("onActivityResult requestCode = %s", requestCode));
        switch (requestCode) {
            case FILECHOOSER_RESULTCODE: {
                if (null == HybridBaseFragment.mUploadMessage
                        && null == HybridBaseFragment.uploadMessageAboveL) {
                    return;
                }
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                if (HybridBaseFragment.uploadMessageAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, intent);
                } else if (HybridBaseFragment.mUploadMessage != null) {
                    HybridBaseFragment.mUploadMessage.onReceiveValue(result);
                    HybridBaseFragment.mUploadMessage = null;
                }
                return;
            }
            default:
        }

        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_PIC_REQUEST_CODE:
                // 选择图片返回路径集合
                ModuleHybridManager.getInstance().getService().onActivityResultSelectedPhotos(mHybridParamUpload, intent);
                break;
            case LOGIN_REQUEST_CODE:
                if (mHybridLoginParam != null) {
                    handleHybridCallback(mHybridLoginParam);
                }
                break;
            default:
                break;
        }
    }

    private void handleHybridCallback(HybridParamCallback callback) {
        if (fragment != null) {
            ((HybridWebViewFragment) fragment).handleHybridCallback(callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (HybridBaseFragment.uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
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
        }
        HybridBaseFragment.uploadMessageAboveL.onReceiveValue(results);
        HybridBaseFragment.uploadMessageAboveL = null;
    }

    // 选择图片
    public void selectPhoto(HybridParamUpload upload) {
        mHybridParamUpload = upload;
        // 选择图片
        ModuleHybridManager.getInstance().getService().gotoSelectPhoto(this, upload.getCount(), SELECT_PIC_REQUEST_CODE);
    }

    public void startLoginActivityForResult(HybridParamLog loginParam) {
        mHybridLoginParam = loginParam;
        ModuleHybridManager.getInstance().getService().gotoLogin(this, loginParam, LOGIN_REQUEST_CODE);
    }

    @Override
    public void finish() {
        super.finish();
//        HybridParamAnimation animation = (HybridParamAnimation) getIntent().getSerializableExtra(HybridConstant.INTENT_EXTRA_KEY_ANIMATION);
//        if (null == animation || animation.equals(HybridParamAnimation.PUSH)) {
//            overridePendingTransition(R.anim.hybrid_left_in, R.anim.hybrid_right_out);
//        } else if (animation.equals(HybridParamAnimation.POP)) {
//            overridePendingTransition(R.anim.hybrid_right_in, R.anim.hybrid_left_out);
//        } else if (animation.equals(HybridParamAnimation.PRESENT)) {
//            overridePendingTransition(R.anim.hybrid_top_in, R.anim.hybrid_bottom_out);
//        }
    }

    @Override
    public void onBackPressed() {
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }


    /**
     * 发送loading提示广播
     *
     * @param id
     * @param display
     */
    public static void postLoadingEvent(int id, boolean display) {
        LogUtil.i(TAG, String.format(" postLoadingEvent id = %s, display = %s", id, display));
        HybridParamShowLoading hybridParam = new HybridParamShowLoading();
        hybridParam.id = id;
        hybridParam.action = display ? HybridParamShowLoading.ACTION_SHOW : HybridParamShowLoading.ACTION_HIDE;
        hybridParam.type = HybridParamShowLoading.TYPE_PROGRESS;//默认只有progress
        EventBus.getDefault().post(hybridParam);
    }

    // Workaround appcompat-1.1.0 bug https://issuetracker.google.com/issues/141132133
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 25) {
            return;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
}