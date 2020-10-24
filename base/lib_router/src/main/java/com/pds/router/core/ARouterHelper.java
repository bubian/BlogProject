package com.pds.router.core;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;

import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.module.BundleKey;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 7:25 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 注意：用navigation()部分手机崩溃，提示请求Intent.FLAG_ACTIVITY_NEW_TASK参数
 */
public class ARouterHelper {
    public static void nav(Activity activity, String url) {
        ARouter.getInstance().build(url).navigation(activity);
    }

    public static void nav(Activity activity, String url,String value) {
        ARouter.getInstance().build(url).withString(BundleKey.PARAM,value).navigation(activity);
    }

    public static void nav(Activity activity, String url,String param,String extra) {
        ARouter.getInstance().build(url)
                .withString(BundleKey.PARAM,param)
                .withString(BundleKey.EXTRA,extra)
                .navigation(activity);
    }

    public static void navAnim(Activity activity, String url,int enter,int out) {
        ARouter.getInstance().build(url).withTransition(enter,out).navigation(activity);
    }

    public static void navAnim(Activity activity, String url, View v) {
        // 转场动画(API16+)
        // // ps. makeSceneTransitionAnimation 使用共享元素的时候，需要在navigation方法中传入当前Activity
        ActivityOptionsCompat compat = ActivityOptionsCompat.
                makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
        ARouter.getInstance().build(url).withOptionsCompat(compat).navigation(activity);
    }

    /**
     * 使用绿色通道(跳过所有的拦截器)
     * @param activity
     * @param url
     */
    public static void navSkip(Activity activity, String url) {
        ARouter.getInstance().build(url).greenChannel().navigation(activity);
    }

    /**
     * 构建标准的路由请求，startActivityForResult
     * @param activity
     * @param url
     * @param code 第二个参数则是RequestCode
     */
    public static void nav(Activity activity, String url,int code) {
        ARouter.getInstance().build(url).navigation(activity,code);
    }

    public static void nav(Activity activity, Uri uri){
        ARouter.getInstance().build(uri).navigation(activity);
    }

    public static void nav(Activity activity, String url, NavCallback callback){
        ARouter.getInstance().build(url).navigation(activity);
    }
}
