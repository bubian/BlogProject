package com.pds.util.safe;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by heaven7 on 2016/4/21.
 */
public class En {

    public static final String DEFAULT_CHARSET = "utf-8";
    public static final int LENG = 128;

    /**
     * @param filePath the file en encyrpt
     * @param model  ENCRYPT or DECRYPT
     * @return the encoded byte array. or null if encrypt failed.
     */
    public static byte[] encryptionFile(String filePath, String key, int model){
        byte[] keys;
        try {
            keys = key.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if(filePath == null){
            return null;
        }
        File file = new File(filePath);
        if(!file.exists() || !file.canWrite() || !file.canRead()){
            return null;
        }
        byte[] arrayOfByte = new byte[LENG];
        RandomAccessFile localRandomAccessFile = null;
        try {
            localRandomAccessFile = new RandomAccessFile(file, "rw");
            localRandomAccessFile.read(arrayOfByte, 0, arrayOfByte.length);
            localRandomAccessFile.seek(0L);
            if (localRandomAccessFile.length() > arrayOfByte.length) {
                arrayOfByte = encryption(arrayOfByte, keys, model);
                localRandomAccessFile.write(arrayOfByte);
            }else{
                return null;
            }
            return arrayOfByte;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(localRandomAccessFile!=null){
                try {
                    localRandomAccessFile.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 加密
     */
    private static final int ENCRYPT = 1;
    /**
     * 解密
     */
    private static final int DECRYPT = -1;

    /**
     * 字节加密方法
     *
     * @param msg
     *            byte,明文
     * @param key
     *            byte,密钥
     * @param method
     *            使用类中静态常量操作标记 Encrytpion.ENCRYPT及 Encryption.DECRYPT
     * @return 返回密文字节
     */
    private static byte encryption(int msg, int key, int method) {
        // key的预处理，处理结果0<=key&&key<8
        key = (byte) (method * (key % 8));
        key = (byte) (key >= 0 ? key : key + 8);

        // msg的预处理，避免msg首位为1
        int nMsg = msg & 255;
        // 移位掩码，tmp保存低位溢出信息
        int mask = (int) Math.pow(2, key) - 1;
        int tmp = nMsg & mask;

        // 循环移位
        nMsg >>= key;
        tmp <<= (8 - key); // 低位溢出信息移至高位
        nMsg |= tmp;
        return (byte) (255 & nMsg); // 取出低8位
    }

    /**
     * 加密解密。 特别说明：利用new String(byte[])方法将密文转换为String后，再使用
     * 其getBytes()方法转换为byte[]并译码，不能得出正确结果！
     *
     * @param msg
     *            byte[]，明文字节数组
     * @param key
     *            byte[]，密钥字节数组
     * @param model
     *            ENCRYPT:加密, DECRYPT:解密
     * @return 返回密文字节数组
     */
    public static byte[] encryption(byte[] msg, byte[] key,int model) {
        byte[] nMsg = new byte[msg.length];
        int len = msg.length;
        for (int i = 0; i < len; i++) {
            nMsg[i] = encryption(msg[i], key[i % key.length], model);
        }
        return nMsg;
    }

    public static byte encode(int msg, byte[] key, int index) {
        return encryption(msg, key[index % key.length], ENCRYPT);
    }

    public static byte[] encode(byte[] msg, byte[] key) {
        return encryption(msg, key, ENCRYPT);
    }

    public static byte decode(int msg, byte[] key, int index) {
        return encryption(msg, key[index % key.length], DECRYPT);
    }

    public static byte[] decode(byte[] msg, byte[] key) {
        return encryption(msg, key, DECRYPT);
    }

    public static byte[] encodeFile(String filePath , String key) {
        return encryptionFile(filePath, key , ENCRYPT);
    }

    public static byte[] decodeFile(String filePath, String key) {
        return encryptionFile(filePath, key, DECRYPT);
    }

    public static class DecodeInputStream extends FileInputStream {

        private static final int CODE_LENGTH = LENG;
        public long currentReadIndex = 0;
        private byte[] mKeys;

        public DecodeInputStream(File file, String key) throws FileNotFoundException {
            super(file);
            getKeys(key);
        }

        private void getKeys(String key) {
            try {
                mKeys = key.getBytes(En.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public DecodeInputStream(FileDescriptor fdObj , String key) {
            super(fdObj);
            getKeys(key);
        }

        public DecodeInputStream(String name , String key) throws FileNotFoundException {
            super(name);
            getKeys(key);
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if(b > 0 && currentReadIndex < CODE_LENGTH){
                b = En.decode(b, mKeys,(int)currentReadIndex);
            }
            currentReadIndex++;
            return b;
        }

        private void deCode(byte[] b,int len){
            if(len > 0 && currentReadIndex < CODE_LENGTH){
                int dl = Math.min(len, (int)(CODE_LENGTH - currentReadIndex));
                int cl = (int) currentReadIndex;
                if(dl >0){
                    for(int i=0;i<dl;i++){
                        b[i] = En.decode(b[i], mKeys, cl);
                        cl++;
                    }
                }
            }
            currentReadIndex += len;
        }
        @Override
        public int read(byte[] b) throws IOException {
            int len = super.read(b);
            deCode(b, len);
            return len;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int l = super.read(b, off, len);
            deCode(b, l);
            return l;
        }

        @Override
        public long skip(long n) throws IOException {
            currentReadIndex += n;
            return super.skip(n);
        }
    }
}
