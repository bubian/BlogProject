package com.pds.web.param;

import com.google.gson.annotations.SerializedName;

public class HybridParamPop extends HybridParamCallback {

    @SerializedName("num")
    private int num = -1;//默认等于-1，无效值。

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
