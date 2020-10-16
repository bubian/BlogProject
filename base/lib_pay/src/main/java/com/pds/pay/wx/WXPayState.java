package com.pds.pay.wx;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/29 3:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class WXPayState {

    /**
     * 签名信息不匹配
     */
    public static final int ERROR_SIGN = -8;

    /**
     * 上一笔支付正在交易中
     */
    public static final int ERROR_NOT_INSTALL_WX = -7;

    /**
     * 上一笔支付正在交易中
     */
    public static final int ERROR_PAYING = -6;
    /**
     * 当前设备不支持微信支付
     */
    public static final int ERROR_NOT_SUPPORT = -5;
    /**
     * 无法创建WXAPI
     */
    public static final int ERROR_NOT_API = -4;

    /**
     * json参数异常
     */
    public static final int ERROR_JSON = -3;

    /**
     * 小程序launch中
     */
    public static final int WX_MIN_LAUNCHING = -9;


    public static String getTipMsg(int code) {
        switch (code) {
            case ERROR_PAYING:
                return "正在支付，请稍后";
            case ERROR_NOT_SUPPORT:
                return "设备不支持微信支付";
            case ERROR_NOT_API:
                return "无法创建微信API";
            case ERROR_JSON:
                return "参数异常";
            case ERROR_NOT_INSTALL_WX:
                return "未安装微信";
            case ERROR_SIGN:
                return "签名信息不匹配";
            default:
                return "";
        }
    }
}
