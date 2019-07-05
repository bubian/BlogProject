package com.pds.util.safe;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import com.pds.util.app.PackageUtils;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetPublicKey {

    /**
     * 获取签名公钥
     * @param mContext
     * @return
     */
    public static String getSignInfo(Context mContext) {
        String signcode = "";
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
                    PackageUtils.getPackageName(mContext), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            signcode = parseSignature(sign.toByteArray());
            signcode = signcode.toLowerCase();
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        }
        return signcode;
    }

    protected static String parseSignature(byte[] signature) {
        String sign = "";
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            Log.e("pubKey = ", pubKey);
            String ss = subString(pubKey);
            ss = ss.replace(",", "");
            ss = ss.toLowerCase();

            Log.e("pubKey == ", ss);

            int aa = ss.indexOf("modulus");
            int bb = ss.indexOf("publicexponent");
            sign = ss.substring(aa + 8, bb);

            Log.e("pubKey === ", sign);
        } catch (CertificateException e) {
            Log.e("", e.getMessage(), e);
        }
        return sign;
    }

    public static String subString(String sub) {
        Pattern pp = Pattern.compile("\\s*|\t|\r|\n");
        Matcher mm = pp.matcher(sub);
        return mm.replaceAll("");
    }



    public static String getApplicationPackage(Context context) {
        try {
            // 通过包管理器获取指定包名包含签名的信息
            Signature signature = context.getPackageManager().getPackageInfo(PackageUtils.getPackageName(context), PackageManager.GET_SIGNATURES).signatures[0];
            MessageDigest messageDigestMd5 = MessageDigest.getInstance("MD5");
            MessageDigest messageDigestSHA1 = MessageDigest.getInstance("SHA1");
            // 通过 MessageDigest这个类来分别取出 Signature中存储的MD5和SHA1
            messageDigestMd5.update(signature.toByteArray());
            messageDigestSHA1.update(signature.toByteArray());
            // 把MessageDigest中存储的md5和sha1转为字符串
            String md5 = toHextring(messageDigestMd5.digest());
            String sha1 = toHextring(messageDigestSHA1.digest());

            String d5 = "md5 = "+md5+ "/n sha1 = " + sha1;
            Log.e("pubKey === d5 = ", d5);
            return d5;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未找到这个包名" ;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "未找到对应的算法" ;
        }
    }

    private static String toHextring(byte[] block) {
        StringBuffer buf = new StringBuffer();
        for (byte aBlock : block) {
            byte2Hex(aBlock, buf);
        }
        return buf.toString();
    }
    private static void byte2Hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0','1' ,'2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }


}
