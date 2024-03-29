package com.pds.util.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志文件切割.防止过大
 * Created by heaven7 on 2016/5/6.
 */
public class LogFileCutUtil {


    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd___HH:mm:ss", Locale.CHINA);
    private static final long LOG_SIZE_LIMIT = 3 * 1024 * 1024 ; // 2MB

    public  static String getLogFilename(String dir){
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        final File[] files = dirFile.listFiles();
        if(files == null || files.length == 0){
            return dir+ "/log_"+ FORMAT.format(new Date(System.currentTimeMillis()))+".txt";
        }
        File f = null;
        for(int i=0,size = files.length ;i<size ;i++){
              if(files[i].isFile() && files[i].length() < LOG_SIZE_LIMIT){
                  f = files[i];
                  break;
              }
        }
        if(f == null){
            return dir+ "/log_"+ FORMAT.format(new Date(System.currentTimeMillis()))+".txt";
        }
        return f.getAbsolutePath();
    }
}
