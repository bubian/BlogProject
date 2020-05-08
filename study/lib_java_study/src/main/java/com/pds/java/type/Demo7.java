package com.pds.java.type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-07 15:04
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.*;

import java.io.Serializable;

public class Demo7 {
    /**
     * 通用的返回值类型
     *
     * @param <T>
     */
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result<T> implements Serializable { //@1
        private String code;
        private String subCode;
        private String msg;
        private T data;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserModel { //@2
        private Integer id;
        private String name;
    }

    /**
     * 返回一个用户信息
     *
     * @return
     */
    public static Result<UserModel> getUser() { //@3
        UserModel userModel = UserModel.builder().id(1).name("路人甲Java").build();
        Result<UserModel> result = Result.<UserModel>builder().code("1").subCode(null).msg("操作成功").data(userModel).build();
        return result;
    }

    /**
     * 用户json信息
     *
     * @return
     */
    public static String getUserString() { //@4
        return JSON.toJSONString(getUser());
    }

    public static void main(String[] args) {
        String userString = getUserString();
        //会输出：{"code":"1","data":{"id":1,"name":"路人甲Java"},"msg":"操作成功"}
        System.out.println(userString); //@5

        //下面我们需要将userString反序列化为Result<UserModel>对象
        Result<UserModel> userModelResult = JSON.parseObject(userString, new TypeReference<Result<UserModel>>() {
        }); //@6

        //我们来看看Result中的data是不是UserModel类型的
        System.out.println(userModelResult.getData().getClass()); //@6
    }
}
