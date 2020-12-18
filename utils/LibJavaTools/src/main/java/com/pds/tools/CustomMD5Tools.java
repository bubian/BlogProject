package com.pds.tools;


import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/12/16 1:45 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class CustomMD5Tools {
    public static String MD5_1(String str) {
        String ss = "dwsad3$#@$!!@#^%&$_";
        String ssss = str + ss;
        System.out.println("--->ssss:" + ssss);
        return DigestUtils.md5Hex(ssss);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new TreeMap();
        map.put("timestamp", 1608185311412L);
        map.put("userAccount", "15708468804");
        map.put("password", "6911E77C594178536EDC1A575E8EF91E");
        String gs = new Gson().toJson(map);

        int len = gs.length();
        byte index = (byte) -((len + 26) & 0xFFFFFFF8);
        System.out.println("--->index:" + index);

        String s = MD5_1(gs);
        System.out.println("--->" + s);
        String def = String.format("%2.2s", s).toLowerCase();
        System.out.println("--->" + def);
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }
}
