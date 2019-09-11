package com.pds.ui.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pds.ui.R;
import com.pds.util.permission.SpecialPermissionsCheckUtils;
import com.pds.util.unit.UnitConversionUtils;

/**
 * CreateTime:  2016/9/21 10:22
 * Email：pdview@163.com.
 * Description: 悬浮在应用最上层弹框
 *
 * @author pengdaosong
 */
public class WindowUi {

    private static final String TAG = "WindowUi";
    /**
     * toast展示的时间
     */
    private final int TOAST_SHOW_LENGTH = 2000 * 2;
    private WindowManager mWdm;
    private View mToastView;
    private WindowManager.LayoutParams mParams;
    /**
     * 记录当前Toast的内容是否已经在显示
     */
    private boolean mIsShow;
    private Context mContext;
    private TextView tv_uploading_tip;
    private LinearLayout ll_status;

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

    public WindowUi(Context context) {
        mIsShow = false;
        mContext = context;
        initView();
        initParams(true);
    }

    private boolean isO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private void initView() {
        mToastView = LayoutInflater.from(mContext).inflate(R.layout.toast_window, null);
        tv_uploading_tip = mToastView.findViewById(R.id.tv_uploading_tip);
        ll_status = mToastView.findViewById(R.id.ll_status);
    }

    private void initParams(boolean clickable) {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;

        //设置进入退出动画效果
        mParams.windowAnimations = R.style.topicAnim;

//        if (isO()) {
//            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else if (DeviceTypeUtils.isMIUI()) {
//            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        } else {
//            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        }

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
        tv_uploading_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "_______>:function:onclick");
            }
        });

        ll_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "_______>:function:onclick");
            }
        });
    }

    /**
     * 通过直接向WindowManager添加window的方式
     */
    private void window() {
        if (mWdm == null) {
            mWdm = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }

        Log.e(TAG, "_______>:WindowManager instance = " + mWdm.toString());

        final ViewParent parent = mToastView.getParent();
        if (parent != null) {
            mWdm.removeViewImmediate(mToastView);
        }
        mWdm.addView(mToastView, mParams);
        mToastView.postDelayed(cancelRunnable, TOAST_SHOW_LENGTH);
    }

    /**
     * toast的方式
     */
    private void toast() {
        Toast toast = new Toast(mContext.getApplicationContext());
        toast.setView(mToastView);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(TOAST_SHOW_LENGTH);
        toast.show();
    }

    public void show() {
        //如果Toast没有显示，则开始加载显示
        if (!mIsShow && mToastView != null) {
            mIsShow = true;
        }
        //异常1
//        solveException1();

        safeShow();
    }

    public void safeShow() {
        //如果Toast没有显示，则开始加载显示
        if (!mIsShow && mToastView != null) {
            mIsShow = true;
        }
        boolean permissionCheck = SpecialPermissionsCheckUtils.checkAlertWindowsPermission(mContext);
        if (permissionCheck) {
            doAlert();
        }else {
            doNotAlertPermission();
        }

        window();
    }

    /**
     * 用户已经打开了SYSTEM_ALERT_WINDOW权限
     */
    private void doAlert() {
        if (isO()){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    private void doNotAlertPermission() {

    }


    /**
     *  int res = mPolicy.checkAddPermission(attrs, appOp);
     *  PhoneWindowManager.java - checkAddPermission
     *
     *  1. 不需要特殊权限
     *  if (type < FIRST_SYSTEM_WINDOW || type > LAST_SYSTEM_WINDOW) {
     *      // Window manager will make sure these are okay.
     *      return ADD_OKAY;
     *  }
     *
     *  2. isSystemAlertWindowType - WindowManager.java
     *  isSystemAlertWindowType(type) {
     *      witch (type) {
     *          case TYPE_PHONE:
     *          case TYPE_PRIORITY_PHONE:
     *          case TYPE_SYSTEM_ALERT:
     *          case TYPE_SYSTEM_ERROR:
     *          case TYPE_SYSTEM_OVERLAY:
     *          case TYPE_APPLICATION_OVERLAY:
     *              return true;
     *      }
     *
     *      //需要：android.permission.INTERNAL_SYSTEM_WINDOW权限
     *      return false;
     *   }
     */

    /**
     * exception: Unable to add window -- token null is not valid; is your activity running?
     * cause： Apps targeting SDK above N MR1 （25）cannot arbitrary add toast windows.
     * analysis：token是null,所以在WindowManagerService的addWindow方面里是在（token == null）条件里返回的 'WindowManagerGlobal.ADD_BAD_APP_TOKEN',
     * 而里面都是通过type进行判断的。定位到：
     * <p>
     * if (type == TYPE_TOAST) {
     * // Apps targeting SDK above N MR1 cannot arbitrary add toast windows.
     * if (doesAddToastWindowRequireToken(attrs.packageName, callingUid,
     * parentWindow)) {
     * Slog.w(TAG_WM, "Attempted to add a toast window with unknown token "
     * + attrs.token + ".  Aborting.");
     * return WindowManagerGlobal.ADD_BAD_APP_TOKEN;
     * }
     * }
     * 大概意思就是，在targetSdkVersion >= 26情况下，是不允许将一个View直接添加到系统窗体上的,那么此时不能使用的常用type有：
     * TYPE_TOAST,TYPE_ACCESSIBILITY_OVERLAY,FIRST_APPLICATION_WINDOW ～ LAST_APPLICATION_WINDOW,也暂时不考虑子window类型
     * <p>
     * 使用：TYPE_SYSTEM_ALERT,
     * <p>
     * 华为 - 8.0 - targetSdkVersion = 28
     * <p>
     * 基本都需要 SYSTEM_ALERT_WINDOW权限
     */
    private void solveException1() {
        // Unable to add window -- token null is not valid; is your activity running?
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.token = new Binder();
        // Unable to add window xxx -- permission denied for window 2003(Attempted to add private presentation window to a non-private display)
        // mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 正常 - 需要SYSTEM_ALERT_WINDOW权限
        // mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //异常 - 需要：INTERNAL_SYSTEM_WINDOW权限
        //mParams.type = WindowManager.LayoutParams.LAST_SYSTEM_WINDOW;
        window();
    }
}