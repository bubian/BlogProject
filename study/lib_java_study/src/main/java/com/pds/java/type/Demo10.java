package com.pds.java.type;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public class Demo10<K, V> {

    Map<String, ? extends List<? extends Map<K, V>>>[][] map;

    public static void parseType(Type type, int level) {
        String whileString = whileString(level);
        if (type instanceof GenericArrayType) {
            System.out.println(whileString + "泛型数组类型:" + type);
            parseType(((GenericArrayType) type).getGenericComponentType(), ++level);
        } else if (type instanceof ParameterizedType) {
            System.out.println(whileString + "泛型类型:" + type);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            System.out.println(whileString + "实际类型:" + parameterizedType.getRawType());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            System.out.println(whileString + actualTypeArguments.length + "个泛型参数,如下：");
            int count = 0;
            for (Type actualTypeArgument : actualTypeArguments) {
                if (count++ == 0) {
                    level++;
                }
                parseType(actualTypeArgument, level);
            }
        } else if (type instanceof WildcardType) {
            System.out.println(whileString + "通配符类型:" + type);
            WildcardType wildcardType = ((WildcardType) type);
            System.out.println(whileString + "通配符类型名称:" + wildcardType.getTypeName());
            Type[] upperBounds = wildcardType.getUpperBounds();
            System.out.println(whileString + "上边界列表");
            int count = 0;
            for (Type upperBound : upperBounds) {
                if (count++ == 0) {
                    level++;
                }
                parseType(upperBound, level);
            }
            System.out.println(whileString + "下边界列表");
            Type[] lowerBounds = wildcardType.getLowerBounds();
            for (Type lowerBound : lowerBounds) {
                if (count++ == 0) {
                    level++;
                }
                parseType(lowerBound, level);
            }
        } else if (type instanceof TypeVariable) {
            System.out.println(whileString + "泛型变量类型:" + type);
            TypeVariable typeVariable = ((TypeVariable) type);
            Type[] bounds = typeVariable.getBounds();
            System.out.println(whileString + "泛型变量上边界列表");
            int count = 0;
            for (Type bound : bounds) {
                if (count++ == 0) {
                    level++;
                }
                parseType(bound, level);
            }
        } else if (type instanceof Class) {
            System.out.println(whileString + "普通类型:" + ((Class) type).getName());
        }
    }

    public static String whileString(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("----");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws NoSuchFieldException {
        parseType(Demo10.class.getDeclaredField("map").getGenericType(), 0);
    }
}
