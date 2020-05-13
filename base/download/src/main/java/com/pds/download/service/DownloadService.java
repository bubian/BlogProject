package com.pds.download.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import com.pds.download.DownloadLibrary;
import com.pds.download.http.DownLoadManager;
import com.pds.download.http.DownloadListener;
import com.pds.download.utils.Utils;

import java.io.File;

public class DownloadService extends IntentService {

    private static final int NOTIFY_DOWNLOAD = 0;
    private static final int NOTIFY_FINISH = 1;
    private static final int NOTIFY_FAILURE = 2;
    //下载音乐
    public static final int TYPE_MUSIC = 0;
    //下载视频
    public static final int TYPE_VIDEO = 1;
    //下载图片
    public static final int TYPE_PICTURE = 2;
    //下载apk
    public static final int TYPE_APK = 3;
    //下载其它文件
    public static final int TYPE_FILE = 4;

    private int type;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    createNotification(NOTIFY_DOWNLOAD, 0, null);
                    break;
                case 1:
                    createNotification(NOTIFY_DOWNLOAD, msg.arg1, null);
                    break;
                case 2:
                    createNotification(NOTIFY_FINISH, 0, msg.obj);
                    if (type == TYPE_APK) {
                        ApkDownLoadListener apkDownLoadListener = DownloadLibrary.getApkDownLoadListener();
                        if (apkDownLoadListener != null) {
                            apkDownLoadListener.downLoadSuccess((String) msg.obj);
                        }
                    }
                    break;
                case 3:
                    createNotification(NOTIFY_FAILURE, 0, null);
                    break;
            }
            return true;
        }
    });

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra("url");
            type = intent.getIntExtra("type", 4);
            String[] split = url.split("/");
            int length = split.length;
            if (length <= 0) return;
            String fileName = split[length - 1];
            switch (type) {
                case TYPE_MUSIC:
                    download(url, DownloadLibrary.MUSIC_PATH + fileName);
                    break;
                case TYPE_VIDEO:
                    download(url, DownloadLibrary.VIDEO_PATH + fileName);
                    break;
                case TYPE_PICTURE:
                    download(url, DownloadLibrary.PICTURE_PATH + fileName);
                    break;
                case TYPE_APK:
                    download(url, DownloadLibrary.APK_PATH + fileName);
                    break;
                case TYPE_FILE:
                    download(url, DownloadLibrary.FILE_PATH + fileName);
                    break;
            }
        }
    }

    /**
     * 下载
     *
     * @param url
     * @param filePath 文件保存全路径
     */
    private void download(String url, final String filePath) {
        DownLoadManager.downLoadFile(url, filePath, new DownloadListener() {

            int progress = 0;

            @Override
            public void onStartDownload() {
                Message message = Message.obtain(mHandler);
                message.what = 0;
                message.sendToTarget();
            }

            @Override
            public void onProgress(final int progress) {
                if (this.progress != progress) {
                    this.progress = progress;
                    Message message = Message.obtain(mHandler);
                    message.what = 1;
                    message.arg1 = progress;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFinishDownload(boolean isHasLocation) {
                Message message = Message.obtain(mHandler);
                message.what = 2;
                message.obj = filePath;
                message.sendToTarget();
                if (type == TYPE_VIDEO) {
                    Utils.insertIntoMediaStore(DownloadService.this, true, new File(filePath), System.currentTimeMillis());
                } else if (type == TYPE_PICTURE) {
                    Utils.insertIntoMediaStore(DownloadService.this, false, new File(filePath), System.currentTimeMillis());
                } else if (type == TYPE_APK) {

//                    Utils.installApk(DownloadService.this, filePath);
                }
            }

            @Override
            public void onFail(String errorInfo) {
                Message message = Message.obtain(mHandler);
                message.what = 3;
                message.sendToTarget();
            }
        });
    }

    /**
     * 设置通知
     *
     * @param notifyId
     * @param progress
     */
    private void createNotification(int notifyId, int progress, Object obj) {
        switch (notifyId) {
            case NOTIFY_DOWNLOAD:
            case NOTIFY_FINISH:
            case NOTIFY_FAILURE:
            default:
                break;
        }
    }


}
