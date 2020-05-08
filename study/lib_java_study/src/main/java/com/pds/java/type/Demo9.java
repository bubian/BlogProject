package com.pds.java.type;

import java.lang.reflect.*;
import java.util.List;

public class Demo9 {

    List<String> list[]; //@1

    public static void main(String[] args) throws NoSuchFieldException {
        Field list = Demo9.class.getDeclaredField("list");
        //获取字段的泛型类型
        Type genericType = list.getGenericType(); //@2
        //看看字段的具体泛型类型
        System.out.println(genericType.getClass()); //@3
        if (genericType instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            //获取数组的具体类型，具体的类型就是List<String>，这个是个泛型类型，对应java中的ParameterizedType接口
            Type genericComponentType = genericArrayType.getGenericComponentType();//@4
            System.out.println(genericComponentType.getClass());
            if (genericComponentType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericComponentType;
                System.out.println(parameterizedType.getRawType());
                //调用getActualTypeArguments()获取List<String>中尖括号中的参数列表
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();//@5
                for (Type actualTypeArgument : actualTypeArguments) {
                    System.out.println(actualTypeArgument.getTypeName());
                }
                System.out.println(parameterizedType.getOwnerType());
            }

        }
    }

}
