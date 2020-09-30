package com.pds.pay;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;

public class SignUtil {

    public static byte[] getSignature(Context context) {
        try {
            String pkgName = context.getPackageName();
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager
                    .getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            return signatures[0].toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte[] b = md.digest();
            int i;
            StringBuilder sb = new StringBuilder();
            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
