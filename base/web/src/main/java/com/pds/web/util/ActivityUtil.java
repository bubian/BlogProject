package com.pds.web.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pds.web.core.HybridConstant;
import com.pds.web.param.HybridParamAnimation;
import com.pds.web.ui.HybridWebViewActivity;

public class ActivityUtil {

    public static void toSimpleActivity(Context context, Class clazz, HybridParamAnimation animation, Bundle bundle) {
        final Intent intent = new Intent(context, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        appendAnimation(context, animation);
    }

    private static void appendAnimation(Context context, HybridParamAnimation animation) {
//        if (context instanceof Activity) {
//            if (null == animation || animation.equals(HybridParamAnimation.PUSH)) {
//                ((Activity) context).overridePendingTransition(R.anim.hybrid_right_in, R.anim.hybrid_left_out);
//            } else if (animation.equals(HybridParamAnimation.POP)) {
//                ((Activity) context).overridePendingTransition(R.anim.hybrid_left_in, R.anim.hybrid_right_out);
//            } else if (animation.equals(HybridParamAnimation.PRESENT)) {
//                ((Activity) context).overridePendingTransition(R.anim.hybrid_bottom_in, R.anim.hybrid_top_out);
//            }
//        }
    }

    public static void forwardHybridH5(Context context, String url ){
        forwardHybridH5(context, url, true);
    }

    public static void forwardHybridH5(Context context, String url, boolean hasNavigation){
        forwardHybridH5(context, url , HybridParamAnimation.NONE, hasNavigation, false );
    }

    /**
     * @param context
     * @param url
     * @param animation
     * @param hasNavigation 是否有标题栏
     */
    public static void forwardHybridH5(Context context, String url, HybridParamAnimation animation, boolean hasNavigation) {
        forwardHybridH5(context, url, animation, hasNavigation, false);
    }

    /**
     * @param context
     * @param url
     * @param animation
     * @param hasNavigation 是否有标题栏
     * @param isNormal      是否是第三方页面
     */
    public static void forwardHybridH5(Context context, String url, HybridParamAnimation animation, boolean hasNavigation, boolean isNormal) {
        //默认使用pop动画
        if (HybridParamAnimation.NONE.equals(animation)) {
            animation = HybridParamAnimation.PUSH;
        }
        Bundle bundle = new Bundle();
        bundle.putString(HybridConstant.INTENT_EXTRA_KEY_TOPAGE, url);
        bundle.putSerializable(HybridConstant.INTENT_EXTRA_KEY_ANIMATION, animation);
        bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_HASNAVGATION, hasNavigation);
        isNormal = isNormal || !url.contains("medlinker.com");
        bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_ISNORMAL, isNormal);
        ActivityUtil.toSimpleActivity(context, HybridWebViewActivity.class, animation, bundle);
    }

}
