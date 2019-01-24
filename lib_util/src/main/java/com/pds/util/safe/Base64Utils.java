package com.pds.util.safe;

import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

/**
 * @author: pengdaosong
 * CreateTime:  2019/1/24 1:19 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class Base64Utils {

    private static Base64Utils utils = null;

    public static Base64Utils getInstance(){
        if(utils == null){
            synchronized (Base64Utils.class) {
                if(utils == null ){
                    utils = new Base64Utils();
                }
            }
        }
        return utils;
    }

    /**
     * Description:获取文件的大小
     * @param inFile 文件
     * @return 文件的大小
     */
    public int getFileSize(File inFile){
        InputStream in = null;

        try {
            in = new FileInputStream(inFile);
            //文件长度
            int len = in.available();
            return len;
        }catch (Exception e) {
            // TODO: handle exception
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Description:将文件转化为base64
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String file2Base64(File inFile){
        //将文件转化为字节码
        byte [] bytes = copyFile2Byte(inFile);
        if(bytes == null){
            return null;
        }
        //base64,将字节码转化为base64的字符串
        String result = Base64.getEncoder().encodeToString(bytes);
        return result;
    }

    /**
     * Description:将文件转化为字节码
     * @param inFile
     * @return
     */
    private byte [] copyFile2Byte(File inFile){
        InputStream in = null;

        try {
            in = new FileInputStream(inFile);
            //文件长度
            int len = in.available();

            //定义数组
            byte [] bytes = new byte[len];

            //读取到数组里面
            in.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            try {
                if(in != null){
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Description:将字符串转化为文件
     * @param strBase64 base64 编码的文件
     * @param outFile 输出的目标文件地址
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean base64ToFile(String strBase64, File outFile){
        try {
            // 解码，然后将字节转换为文件
            byte[] bytes = Base64.getDecoder().decode(strBase64);
            return copyByte2File(bytes,outFile);
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
    /**
     * Description:将字节码转化为文件
     * @param bytes
     * @param file
     */
    private boolean copyByte2File(byte [] bytes,File file){
        FileOutputStream  out = null;
        try {
            //转化为输入流
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            //写出文件
            byte[] buffer = new byte[1024];

            out = new FileOutputStream(file);

            //写文件
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String decodeBase64(String input) {
        Object retObj= null;
        try {
            Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod= clazz.getMethod("decode", String.class);
            mainMethod.setAccessible(true);
            retObj = mainMethod.invoke(null, input);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new String((byte[])retObj);
    }

    /**
     * BASE64字符串解码为二进制数据
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return android.util.Base64.decode(base64.getBytes(),android.util.Base64.DEFAULT);
    }

    /**
     * 二进制数据编码为BASE64字符串
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(android.util.Base64.encode(bytes, android.util.Base64.DEFAULT));
    }
}
