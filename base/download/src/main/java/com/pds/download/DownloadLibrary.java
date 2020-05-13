package com.pds.download;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.pds.download.service.ApkDownLoadListener;

import java.io.File;

/**
 * author：Created by zhaohaoting on 2019/6/25
 * email：526309416@qq.com
 * desc：必须先初始化此类
 */
public class DownloadLibrary {

    public static void init(Context context, int ic_launcher, Class<? extends Activity> entranceActivity, String parentFileName) {
        DownloadLibrary.context = context;
        DownloadLibrary.ic_launcher = ic_launcher;
        DownloadLibrary.entranceActivity = entranceActivity;
        DownloadLibrary.PARENT_FILE_NAME = parentFileName;
    }

    private static Context context;
    private static int ic_launcher;
    private static Class<? extends Activity> entranceActivity;
    private static ApkDownLoadListener apkDownLoadListener ;

    public static ApkDownLoadListener getApkDownLoadListener() {
        return apkDownLoadListener;
    }

    public static void setApkDownLoadListener(ApkDownLoadListener apkDownLoadListener) {
        DownloadLibrary.apkDownLoadListener = apkDownLoadListener;
    }

    public static String PARENT_FILE_NAME = "download";
    public static String MUSIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PARENT_FILE_NAME + File.separator + "music/";
    public static String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PARENT_FILE_NAME + File.separator + "video/";
    public static String PICTURE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PARENT_FILE_NAME + File.separator + "picture/";
    public static String APK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PARENT_FILE_NAME + File.separator + "apk/";
    public static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PARENT_FILE_NAME + File.separator + "file/";

    public static Context getContext() {
        return context;
    }

    public static int getIc_launcher() {
        return ic_launcher;
    }

    public static Class<? extends Activity> getEntranceActivity() {
        return entranceActivity;
    }
}
