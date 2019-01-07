package com.pds.ui.toast;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.pds.ui.R;
import com.pds.util.DeviceTypeUtils;
import com.pds.util.SpecialPermissionsCheckUtils;
import com.pds.util.UnitConversionUtils;

/**
 * CreateTime:  2016/9/21 10:22
 * Email：pdview@163.com.
 * Description: 悬浮在应用最上层弹框
 *
 * @author pengdaosong
 */
public class UploadingCommunityToast {

    private static final String TAG = "UploadingCommunityToast";

    private WindowManager mWdm;
    private View mToastView;
    private WindowManager.LayoutParams mParams;
    /**
     * 记录当前Toast的内容是否已经在显示
     */
    private boolean mIsShow;
    private Context mContext;
    /**
     * toast展示的时间
     */
    private final int TOAST_SHOW_LENGTH = 2000 * 2;

    private TextView tv_uploading_tip;
    private ImageView iv_upload_tag;
    private LinearLayout ll_status;

    public UploadingCommunityToast(Context context, final UPLOAD_TYPE type, final Integer... ids) {
        //记录当前Toast的内容是否已经在显示
        mIsShow = false;
        this.mContext = context;
        //通过Toast实例获取当前android系统的默认Toast的View布局
        mToastView = LayoutInflater.from(context).inflate(R.layout.toast_window, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, UnitConversionUtils.dip2px(context, 60));
        LinearLayout content = mToastView.findViewById(R.id.ll_uploading);
        content.setLayoutParams(params);

        tv_uploading_tip = mToastView.findViewById(R.id.tv_uploading_tip);
        iv_upload_tag = mToastView.findViewById(R.id.iv_upload_tag);
        ll_status = mToastView.findViewById(R.id.ll_status);

        switch (type) {
            case START: {
                ll_status.setVisibility(View.GONE);
                iv_upload_tag.setVisibility(View.GONE);
                tv_uploading_tip.setText(mContext.getString(R.string.uploading, "文件"));
                break;
            }
            case FAIL: {
                ll_status.setVisibility(isO() ? View.GONE : View.VISIBLE);
                iv_upload_tag.setVisibility(View.VISIBLE);
                iv_upload_tag.setImageResource(R.mipmap.upload_fail);
                tv_uploading_tip.setText(mContext.getString(R.string.upload_failed, "文件"));
                break;
            }
            case SUCCEED: {
                ll_status.setVisibility(isO() ? View.GONE : View.VISIBLE);
                iv_upload_tag.setVisibility(View.VISIBLE);
                iv_upload_tag.setImageResource(R.mipmap.uploading_succ);
                tv_uploading_tip.setText(mContext.getString(R.string.upload_success, "文件"));
                break;
            }
            default:
                break;
        }
        //设置布局参数
        setParams(false);
    }

    private void setParams(boolean clickable) {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        //设置进入退出动画效果
        mParams.windowAnimations = R.style.topicAnim;
        if (isO()) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (DeviceTypeUtils.isMIUI()) {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        if (clickable) {
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        mParams.gravity = Gravity.TOP;
        mParams.x = 0;
        mParams.y = UnitConversionUtils.dip2px(mContext, 50);
    }

    public void show() {
        //如果Toast没有显示，则开始加载显示
        if (!mIsShow && mToastView != null) {
            mIsShow = true;
            if (isO() || (DeviceTypeUtils.isMIUI() && !SpecialPermissionsCheckUtils.checkAlertWindowsPermission(mContext))) {
                Toast toast = new Toast(mContext.getApplicationContext());
                toast.setView(mToastView);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.setDuration(TOAST_SHOW_LENGTH);
                toast.show();
            } else {
                if (mWdm == null) {
                    mWdm = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                }
                Log.e(TAG,"WindowManager instance = " + mWdm.toString());
                final ViewParent parent = mToastView.getParent();
                if (parent != null) {
                    mWdm.removeViewImmediate(mToastView);
                }
                /**
                 * 通过查看Android系统源码，如果mParams的类型是TYPE_TOAST，对于同一个uid，在同一个时刻只能添加一个toast类型的View
                 * 所以有可能会报：view.WindowManager$BadTokenException: Unable to add window -- window android.view.ViewRootImpl$W@370b36c has already been added异常
                 * TODO: 这个地方需要控制上一次添加的toast，所以暂时try..catch..住,后期安排优化
                 */
                mWdm.addView(mToastView, mParams);
                mToastView.postDelayed(cancelRunnable, TOAST_SHOW_LENGTH);

            }
        }
    }

    private Runnable cancelRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mWdm != null && mIsShow) {
                    mWdm.removeViewImmediate(mToastView);
                    mIsShow = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public enum UPLOAD_TYPE {
        //开始上传
        START,
        //失败
        FAIL,
        //成功
        SUCCEED
    }

    private boolean isO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}