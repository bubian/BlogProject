package com.blog.pds.net.exception;

public class ApiException extends RuntimeException {

    protected int mCode;
    private String mMsg;

    public ApiException(String detailMessage) {
        super(detailMessage);
        mMsg = detailMessage;
    }

    public ApiException(int code, String msg) {
        super(msg);
        mCode = code;
        mMsg = msg;
    }

    public int getCode() {
        return mCode;
    }

    public String getMsg() {
        return mMsg;
    }
}
