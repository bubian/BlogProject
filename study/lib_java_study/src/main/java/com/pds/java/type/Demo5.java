package com.pds.java.type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-07 15:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Demo5<T1, T2> { //@1
    public void m1(Demo5<T1, T2> demo) { //@2
        //demo6Class对应的是Demo6的Class对象
        Class<? extends Demo5> demoClass = demo.getClass();
        //获取Demo6的父类的详细类型信息，包含泛型信息
        Type genericSuperclass = demoClass.getGenericSuperclass();
        // 泛型类型用ParameterizedType接口表示，输出看一下是不是这个接口类型的
        System.out.println(genericSuperclass.getClass());
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            System.out.println(pt.getRawType());
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                System.out.println(actualTypeArgument.getTypeName());
            }
            System.out.println(pt.getOwnerType());
        }
    }

    public static void main(String[] args) {
        // 这个地方利用了一个匿名内部类，相当于创建了Demo5的一个子类，并且指定了Demo5中两个泛型变量类型的具体类型。然后用getGenericSuperclass获取当前类父类的具体类型信息。
        // 比如Gson中：ArrayList<TopicEntity> arrayList = new Gson().fromJson(uploadJson, new TypeToken<ArrayList<TopicEntity>>() {}.getType());
        Demo5<String, Integer> demo5 = new Demo5<String, Integer>() {
        };//@3
        demo5.m1(demo5);
    }
}
