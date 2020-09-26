package com.pds.blog.web.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 15:31
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public enum HybridParamAnimation {
    PUSH("push"),//从左边进入,默认动画
    POP("pop"),//从右边出去,
    PRESENT("present"),//从下面进来(这种时候页面默认从下面出去)
    NONE("none"),//没有动画
    ;

    public String mValue;

    HybridParamAnimation(String value) {
        mValue = value;
    }

    public static HybridParamAnimation findByAbbr(String value) {
        for (HybridParamAnimation currEnum : HybridParamAnimation.values()) {
            if (currEnum.mValue.equals(value)) {
                return currEnum;
            }
        }
        return null;
    }

    public static class TypeDeserializer implements JsonDeserializer<HybridParamAnimation> {

        @Override
        public HybridParamAnimation deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String type = arg0.getAsString();
            return HybridParamAnimation.findByAbbr(type);
        }
    }
}