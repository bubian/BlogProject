package com.pds.java.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-07 14:44
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface Demo1I1 { //@1
}

interface Demo1I2 { //@2
}

class Demo1 <T1, T2 extends Integer,T3 extends Demo1I1 & Demo1I2>{

    public static void main(String[] args){
        testTypeParameters();
    }

    /**
     * getTypeParameters:获取声明的泛型变量类型清单.
     * java中任何类可以使用Class对象表示，方法可以用Method类表示，类图中可以知，Class类和Method类实现了GenericDeclaration接口，
     * 所以可以调用他们的getTypeParameters方法获取其声明的泛型参数列表。
     */
    private static void testTypeParameters(){
        TypeVariable<Class<Demo1>>[] typeParameters = Demo1.class.getTypeParameters();
        System.out.println("原始输出:" + typeParameters);
        System.out.println("--------------------");
        for (TypeVariable<Class<Demo1>> typeParameter : typeParameters) {
            System.out.println("变量名称:" + typeParameter.getName());
            System.out.println("这个变量在哪声明的:" + typeParameter.getGenericDeclaration());
            Type[] bounds = typeParameter.getBounds();
            System.out.println("这个变量上边界数量:" + bounds.length);
            System.out.println("这个变量上边界清单:");
            for (Type bound : bounds) {
                System.out.println(bound.getTypeName());
            }
            System.out.println("--------------------");
        }
    }
}
