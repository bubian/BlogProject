package com.pds.entity.base;

import com.google.gson.annotations.SerializedName;

public class BaseEntity<T extends DataEntity> {
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    /**
     * 用于提示用的消息
     */
    @SerializedName("tipMsg")
    private String tipMsg;
    @SerializedName("data")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTipMsg() {
        return tipMsg;
    }

    public void setTipMsg(String tipMsg) {
        this.tipMsg = tipMsg;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", tipMsg=" + tipMsg +
                '}';
    }
}
