package com.pds.util.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * Created by Guest on 15/12/18.
 */
public class ViewUtil {

    public static void obtainFocus(View v) {
        if (v != null) {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.requestFocusFromTouch();
        }
    }

    private static long lastClickTime;

    //editText限制输入字数
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private static long[] mHits = new long[2];

    public static void isFastDoubleClick(Runnable callBack) throws Exception {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//获取手机开机时间
        if (mHits[mHits.length - 1] - mHits[0] < 500) {
            callBack.run();
        }
    }

    /**
     * 返回视图是否位于顶部
     *
     * @param view View
     * @return boolean
     */
//    public static boolean isViewTop(View view) {
//        if (view == null) {
//            return true;
//        }
//        view = findCanScrollView(view);
//        return view == null || !canScrollVertically(view, -1);
//    }

    /**
     * 找到canscroll的View，直到找完改group下的所有view
     *
     * @param view
     * @return
     */
//    private static View findCanScrollView(View view) {
//        if (view == null || !(view instanceof ViewGroup)) {
//            return null;
//        }
//        if (view instanceof ScrollView || view instanceof AbsListView
//                || view instanceof RecyclerView || view instanceof WebView) {
//            return view;
//        }
//        ViewGroup group = (ViewGroup) view;
//        int childCount = group.getChildCount();
//        if (childCount <= 0) {
//            return null;
//        }
//        for (int i = 0; i < childCount; i++) {
//            View result = findCanScrollView(group.getChildAt(i));
//            if (result != null) {
//                return result;
//            }
//        }
//        return null;
//    }


    /**
     * Check if this view can be scrolled vertically in a certain direction.
     *
     * @param view      The View against which to invoke the method.
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public static boolean canScrollVertically(View view, int direction) {
        return view.canScrollVertically(direction);
    }

//    public static void hideSystemUI(Activity activity) {
//        if (activity == null || activity.isDestroyed()) {
//            return;
//        }
//        // Set the IMMERSIVE flag.
//        // Set the content to appear under the system bars so that the content
//        // doesn't resize when the system bars hide and show.
//        if (BuildVersionUtils.hasKitkat()) {
//            View decorView = activity.getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
//            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            activity.getWindow().setAttributes(attrs);
//            activity.getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//    }


//    public static void showSystemUI(Activity activity) {
//        if (activity == null || activity.isDestroyed()) {
//            return;
//        }
//        if (BuildVersionUtils.hasKitkat()) {
//            View decorView = activity.getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        } else {
//            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
//            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            activity.getWindow().setAttributes(attrs);
//            activity.getWindow().clearFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//    }

    /**
     * 抖动view
     *
     * @param view
     * @param duration ms
     */
    public static void shakeView(View view, long duration) {
        TranslateAnimation animation = new TranslateAnimation(0, 10, 0, 0);
        animation.setInterpolator(new CycleInterpolator(3f));
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static Bitmap loadBitmapFromView(View view, int width, int height) {
        if (view == null) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static boolean isActivityDestory(Context context) {
        Activity activity = findActivity(context);
        return activity == null || activity.isDestroyed();
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    /**
     * 设置EditText最大长度
     */
    public static void setMaxLength(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
