package com.pds.pay.ali;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/29 3:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AliPayState {

    /**
     * 上一笔支付正在交易中
     */
    public static final int ERROR_PAYING = 1000;
    /**
     * 获取到的Activity实例是null
     */
    public static final int ERROR_ACTIVITY_NULL = 3000;
    /**
     * 其它不明情况支付失败
     */
    public static final int ERROR_PAY = 2000;

    /**
     * H5支付拦截
     */
    public static final int H5_INTERCEPTED = 4000;

    /**
     * url不合法
     */
    public static final int ERROR_H5_URL = 5000;

    /**
     * 支付成功，具体状态码代表含义可参考接口文档
     */
    public static final int SUCCESS_PAY = 9000;
    /**
     * 判断resultStatus 为非“9000”则代表可能支付失败 “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
     */
    public static final int ERROR_INDETERMINATE = 8000;

    public static String getTipMsg(int code) {
        switch (code) {
            case ERROR_PAYING:
                return "正在支付，请稍后";
            case ERROR_ACTIVITY_NULL:
                return "支付未完成，请重新支付";
            case ERROR_H5_URL:
                return "url不合法";
            default:
                return "";
        }
    }

    public static String getStringState(int code) {
        switch (code) {
            case SUCCESS_PAY:
                return "9000";
            case ERROR_INDETERMINATE:
                return "8000";
            case H5_INTERCEPTED:
                return "4000";
            default:
                return "";
        }
    }
}
