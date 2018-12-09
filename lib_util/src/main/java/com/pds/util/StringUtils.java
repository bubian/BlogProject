package com.pds.util;

/**
 * @author: pengdaosong.
 * CreateTime:  2018/12/9 3:43 PM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class StringUtils {

    public static boolean isStrictEmpty(String s) {
        return s == null || "".equals(s) || "null".equalsIgnoreCase(s);
    }
}
