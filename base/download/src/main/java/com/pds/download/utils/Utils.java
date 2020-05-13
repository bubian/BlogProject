package com.pds.download.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import java.io.File;

public class Utils {

    /**
     * 是否在wifi状态下
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * //把文件插入到系统图库
     * <p>
     * /**
     *
     * @param context
     * @param targetFile 要保存的照片文件
     * @param path       要保存的照片的路径地址
     */
    public static void addMediaStore(Context context, File targetFile, String path) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues newValues = new ContentValues(5);
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, targetFile.getName());
        newValues.put(MediaStore.Images.Media.DATA, targetFile.getPath());
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Images.Media.SIZE, targetFile.length());
        newValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);//刷新相册
    }

    //针对非系统影音资源文件夹
    public static void insertIntoMediaStore(Context context, boolean isVideo, File saveFile, long createTime) {
        ContentResolver mContentResolver = context.getContentResolver();
        if (createTime == 0)
            createTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        //值一样，但是还是用常量区分对待
        values.put(isVideo ? MediaStore.Video.VideoColumns.DATE_TAKEN
                : MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (!isVideo)
            values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ? getVideoMimeType(saveFile.getAbsolutePath()) : "image/jpeg");
        //插入
        mContentResolver.insert(isVideo
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        scanFile(context, saveFile.getAbsolutePath());
    }

    // 获取video的mine_type,暂时只支持mp4,3gp
    private static String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }

    /**
     * 针对系统文夹只需要扫描,不用插入内容提供者,不然会重复
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    private static void scanFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }

    public static void installApk(Context context, String localPath) {
        context.startActivity(getEntranceIntent(context, localPath));
    }

    public static Intent getEntranceIntent(Context context, String locationPath) {
        Intent install = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(locationPath));
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(new File(locationPath)), "application/vnd.android.package-archive");
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setAction(Intent.ACTION_VIEW);
        return install;
    }

    public static void startInstall(final FragmentActivity context, String localPath, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean installAllowed = context.getPackageManager().canRequestPackageInstalls();
            if (installAllowed) {
                context.startActivity(getEntranceIntent(context, localPath));
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("安装应用需要打开未知来源权限，请去设置中开启权限")
                        .setPositiveButton("确定", (d, w) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
                            context.startActivityForResult(intent, requestCode);
                        })
                        .show();
            }
        } else {
            context.startActivity(getEntranceIntent(context, localPath));
        }
    }

}
