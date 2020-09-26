package com.pds.web.param;


import com.google.gson.JsonObject;

public class HybridParamAjax extends HybridParam {
    public ACTION tagname = ACTION.GET;
    public String url;
    public JsonObject param;// this param is json data
    public String callback;

    public enum ACTION {
        GET("get"), POST("post");

        public String mValue;

        ACTION(String value) {
            mValue = value;
        }

        public static ACTION findByAbbr(String value) {
            for (ACTION currEnum : ACTION.values()) {
                if (currEnum.mValue.equals(value)) {
                    return currEnum;
                }
            }
            return GET;
        }
    }

}
