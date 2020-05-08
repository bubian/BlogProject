package com.pds.java.type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-07 15:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

//泛型类
class Demo<T1, T2> {//@0

}

public class Demo6 extends Demo<String, Integer> { //@1

    public static void main(String[] args) {
        Demo6 demo6 = new Demo6();
        //demo6Class对应的是Demo6的Class对象
        Class<? extends Demo6> demo6Class = demo6.getClass();//@2

        //获取Demo6的父类的详细类型信息，包含泛型信息
        Type genericSuperclass = demo6Class.getGenericSuperclass(); //@3
        // 泛型类型用ParameterizedType接口表示，输出看一下是不是这个接口类型的
        System.out.println(genericSuperclass.getClass()); //@4
        if (genericSuperclass instanceof ParameterizedType) { //@5
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            System.out.println(pt.getRawType());
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                System.out.println(actualTypeArgument.getTypeName());
            }
            System.out.println(pt.getOwnerType());
        }
    }

}
