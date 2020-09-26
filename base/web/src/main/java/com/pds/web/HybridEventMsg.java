package com.pds.web;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HybridEventMsg<T> {
    /**
     * 消息类型
     */
    private int mType;
    /**
     * 消息传递实体
     */
    private T mMessage;
    /**
     * 标识参数
     */
    private int mArg1;

    public HybridEventMsg(@HybridEventMsgType int type, T message) {
        this(type, message, 0);
    }

    public HybridEventMsg(@HybridEventMsgType int type, T message, int arg1) {
        this.mType = type;
        this.mMessage = message;
        this.mArg1 = arg1;
    }

    public HybridEventMsg(@HybridEventMsgType int type) {
        this(type, null);
    }

    @HybridEventMsgType
    public int getType() {
        return mType;
    }

    public void setType(@HybridEventMsgType int type) {
        this.mType = type;
    }

    public T getMessage() {
        return mMessage;
    }

    public void setMessage(T message) {
        this.mMessage = message;
    }

    public int getArg1() {
        return mArg1;
    }

    public void setArg1(int arg1) {
        this.mArg1 = arg1;
    }

    // web页面加载完成
    public static final int WEBVIEW_PAGE_LOAD_FINISHED = 1;
    // 医联支付成功刷新支付入口数据即可
    public static final int MEDLINKER_PAY_SUCEED = 2;


    @IntDef({
            WEBVIEW_PAGE_LOAD_FINISHED,
            MEDLINKER_PAY_SUCEED,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HybridEventMsgType {
    }
}
