package com.pds.tools.main;

import com.pds.tools.DesEncryptUtil;
import com.pds.tools.FileTools;
import com.pds.tools.MD5Tools;
import com.pds.tools.ZipTools;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;

/**
 * @author: pengdaosong CreateTime:  2020-04-07 13:37 Email：pengdaosong@medlinker.com Description:
 */
public class AssetsEncrypMain {

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String SRC_PATH = USER_DIR + "/app/src/main/assets/richEditor/";
    private static final String DEST_PATH= SRC_PATH + "0000.zip";
    private static final String DEST_ENCRYP_PATH = USER_DIR +  "/app/src/main/assets/rich_editor";

    private static final Key KEY = DesEncryptUtil.getKey("medrn1580977272874");

    public static void main(String[] args) {
//        FileTools.deleteFiles(DEST_PATH);
//        try {
//            ZipTools.zipEncrypt(SRC_PATH,DEST_PATH,null,true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            DesEncryptUtil.encryptFile(new File(DEST_PATH),new File(DEST_ENCRYP_PATH),KEY);
//            FileTools.deleteFiles(DEST_PATH);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        decode(DesEncryptUtil.getKey("medrn1580977272874"));
    }


    private static void decode(Key key){
        File src = new File(USER_DIR + "/utils/LibJavaTools/android.meta");
        File de = new File(USER_DIR + "/utils/LibJavaTools/android_de.meta");
        try {
            if (!de.exists()){
                de.createNewFile();
            }
            DesEncryptUtil.decryptFile(src,de,key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decode(){
        File de = new File(USER_DIR + "/LibJavaTools/de.zip");
        try {
            if (!de.exists()){
                de.createNewFile();
            }
            DesEncryptUtil.decryptFile(new File(DEST_ENCRYP_PATH),de,KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encrypOne(){
        String HASH_FILE = SRC_PATH + "hash";
        File f = new File(SRC_PATH);
        if (!f.exists() || f.isFile()) { return; }
        File[] files = f.listFiles();
        if (null == files || files.length < 1) { return; }
        File hashFile = new File(HASH_FILE);
        if (!hashFile.exists()) {
            try {
                boolean re = hashFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        FileTools.deleteOnlyFile(HASH_FILE);
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            if (file.isFile()) {
                String name = FileTools.getFileName(file);
                String fullName = file.getName();
                System.out.println("main file name = " + name);
                String md5 = MD5Tools.getFileMD5(file);
                builder.append(fullName);
                builder.append(":");
                builder.append(md5);
                builder.append("\n");

                try {
                    File dest = new File(HASH_FILE + name);
                    DesEncryptUtil.encryptFile(file, dest, KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(hashFile);
            outputStream.write(builder.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
