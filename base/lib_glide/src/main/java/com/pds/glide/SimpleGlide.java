package com.pds.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pds.glide.shapetransform.GlideCircleBorderTransform;
import com.pds.glide.shapetransform.GlideCircleTransform;

public class SimpleGlide {

    /**
     * 带圆角处理的Options
     * @return
     */
    public static RequestOptions getRoundedCornersOptions(int roundingRadius) {
        if (roundingRadius <= 0) {
            return new RequestOptions();
        }
        return new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(roundingRadius));
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 设置图片
     *
     * @param imgResId
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, int imgResId) {
        if (null == targetImageView) {
            return;
        }

        Glide.with(targetImageView).load(imgResId)
                .into(targetImageView);
    }


    /**
     * 设置图片
     *
     * @param imgResId
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, String imgResId, int placeHolderResId) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView).load(imgResId)
                .apply(new RequestOptions().placeholder(placeHolderResId))
                .into(targetImageView);
    }

    /**
     * 设置图片
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, String imgResId, int placeHolderResId, int errorResId) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView).load(imgResId)
                .apply(new RequestOptions().placeholder(placeHolderResId).error(errorResId))
                .into(targetImageView);
    }

    /**
     * 设置固定大小的图片
     *
     * @param internetUrl
     * @param width
     * @param height
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageViewWithHW(ImageView targetImageView, String internetUrl, int width, int height) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(internetUrl)
                .apply(new RequestOptions().override(width, height))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param internetUrl
     * @param targetImageView
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, String internetUrl) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(internetUrl)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param internetResid
     * @param targetImageView
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, int internetResid) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(internetResid)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片 带边框
     *
     * @param targetImageView
     * @param internetUrl
     * @param placeHolderResId
     * @param borderColor
     * @param borderWidth      单位dp
     */
    @Deprecated
    public static void setCircleImageViewWithBorder(ImageView targetImageView, String internetUrl, int placeHolderResId, int borderWidth, int borderColor) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView).load(internetUrl)
                .apply(new RequestOptions().transform(new GlideCircleBorderTransform(targetImageView.getContext(), borderWidth, borderColor)))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param targetImageView
     * @param bytes
     * @param placeHolderResId
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, byte[] bytes, int placeHolderResId) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView)
                .load(bytes)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片 带边框
     *
     * @param targetImageView
     * @param bytes
     * @param placeHolderResId
     * @param borderColor
     * @param borderWidth      单位dp
     */
    @Deprecated
    public static void setCircleImageViewWithBorder(ImageView targetImageView, byte[] bytes, int placeHolderResId, int borderWidth, int borderColor) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(bytes)
                .apply(new RequestOptions().placeholder(placeHolderResId).transform(new GlideCircleBorderTransform(targetImageView.getContext(), borderWidth, borderColor)))
                .into(targetImageView);
    }

    /**
     * 设置指定圆角的图片
     *
     * @param internetUrl
     * @param targetImageView
     */
    @Deprecated
    public static void setRoundImageView(ImageView targetImageView, String internetUrl, int cornerRadios) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(internetUrl)
                .apply(getRoundedCornersOptions(dip2px(targetImageView.getContext(), cornerRadios)))
                .into(targetImageView);
    }


    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl        头像地址
     * @param placeHolderResId 头像展位图
     * @param targetImageView  目标控件
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, String headerUrl, int placeHolderResId) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(headerUrl)
                .apply(new RequestOptions().placeholder(placeHolderResId).transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl        头像地址
     * @param placeHolderResId 头像展位图
     * @param targetImageView  目标控件
     */
    @Deprecated
    public static void setHeaderCircleImageView(ImageView targetImageView, String headerUrl, int placeHolderResId) {
        if (null == targetImageView) {
            return;
        }
        Glide.with(targetImageView.getContext())
                .load(headerUrl)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }
}

