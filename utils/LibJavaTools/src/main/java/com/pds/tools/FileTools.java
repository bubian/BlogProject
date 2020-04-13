package com.pds.tools;

import java.io.File;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-07 14:38
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class FileTools {


    public static void deleteFiles(String path){
        if (StringTools.isEmpty(path)){ return; }
        File file = new File(path);
        deleteFiles(file);
    }

    public static void deleteOnlyFile(String path){
        if (StringTools.isEmpty(path)){ return; }
        File file = new File(path);

        File[] files = file.listFiles();
        if (null == files){return;}
        for (File f : files) {
            if (f.isFile()){
                f.delete();
            }
        }
    }

    public static void deleteFiles(File files){
        if (!files.exists()){
            return;
        }
        if (files.isFile()){
            files.delete();
        }else {
            for (File f : files.listFiles()) {
                deleteFiles(f);
            }
        }
    }

    public static String getFileName(File file){
        if (null == file){
            return "";
        }
        String s = file.getName();
        int index = s.lastIndexOf(".");
        if (index < 0){
            return "";
        }
        return s.substring(0,index);
    }
}
