package com.pds.download.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.pds.download.DownloadLibrary;
import com.pds.download.R;
import com.pds.download.entity.Version;
import com.pds.download.http.DownLoadManager;
import com.pds.download.http.DownloadListener;
import com.pds.download.service.ApkDownLoadListener;
import com.pds.download.service.DownloadService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * author：Created by zhaohaoting on 2019/6/25
 * email：526309416@qq.com
 * desc：
 */
public class VersionUpdateUtils {
    private Context mContext;
    private final FragmentManager fragmentManager;
    private boolean isServiceDownload = true; //是否开启服务后台下载

    public VersionUpdateUtils(FragmentActivity mContext, boolean isServiceDownload, TipsDialogProvider tipsDialogProvider) {
        this.mContext = mContext;
        fragmentManager = mContext.getSupportFragmentManager();
        this.tipsDialogProvider = tipsDialogProvider;
        this.isServiceDownload = isServiceDownload;
    }

    /**
     * 检查新版本
     *
     * @param currentVersion 服务最新版本
     */
    public void checkNewVersion(Version currentVersion) {

        try {
            if (currentVersion.getReleaseCode().compareTo(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName) < 1) {
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        updateVersion(currentVersion);
    }

    /**
     * 更新
     *
     * @param version
     */
    private void updateVersion(final Version version) {
        //若数据库缓存的应用版本号小于或等于了当前应用版本号，直接返回出去

        //保存下载链接
        String url = version.getUrl();
        if (url == null) return;
        if (version.isMandatory()) {
            forceUpdate(version);
        } else {
            String versionCode = VersionPreferences.getString(VersionPreferences.VERSION_CODE, "0");
            //                    /**（一天提示一次）*/
//                    if (version.getTipsTime().equals(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(System.currentTimeMillis()))) return;
            /**(一个版本提示一次)*/
            if (!TextUtils.isEmpty(versionCode) && version.getReleaseCode().compareTo(versionCode) < 1) {
                return;
            } else {
                VersionPreferences.saveString(VersionPreferences.VERSION_CODE, version.getReleaseCode());
//            delLocationNewPackage(url);
                //静默下载安装包
                if (Utils.isWifi(mContext)) {//如果在wifi状态下自动更新  否则提示用户
                    startService(version);
                } else {
                    //提示有新版本
                    if (tipsDialogProvider != null) {
                        tipsDialogProvider.getVersionUpdateDialog(version, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newVersionUrl = version.getUrl();
                                if (TextUtils.isEmpty(newVersionUrl)) return;
                                if (isServiceDownload) {
                                    Toast.makeText(mContext, "已加入下载队列", Toast.LENGTH_SHORT).show();
                                    startService(version);
                                } else {
                                    showDownloadDialog(newVersionUrl);
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VersionPreferences.saveString(VersionPreferences.TIPS_TIME, new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(System.currentTimeMillis()));
                                dialog.dismiss();
                            }
                        }).show(fragmentManager, " tipsDialog");
                    }
                }
            }
        }
    }

    /**
     * 启动后台下载service
     *
     * @param version
     */
    private void startService(Version version) {
        Intent service = new Intent(mContext, DownloadService.class);
        service.putExtra("url", version.getUrl());
        service.putExtra("type", DownloadService.TYPE_APK);
        mContext.startService(service);
    }

    private TipsDialogProvider tipsDialogProvider;

    public void setTipsDialogProvider(TipsDialogProvider tipsDialogProvider) {
        this.tipsDialogProvider = tipsDialogProvider;
    }

    public interface TipsDialogProvider {

        DialogFragment getVersionUpdateDialog(Version version, DialogInterface.OnClickListener confirmClickListener, DialogInterface.OnClickListener cancelClickListener);

        DialogFragment getForceUpdateDialog(Version version, DialogInterface.OnClickListener onClickListener);
    }

    /**
     * 是否需要强制更新
     *
     * @param version
     * @return
     */
    private boolean isForceUpdateNewVersion(final Version version) {
        return version.isMandatory();
    }

    /**
     * 判断本地是否有最新安装包
     *
     * @param url
     * @return
     */
    private void delLocationNewPackage(final String url) {
        String apklocalUrl = getApkLocationPath(url);
        if (!TextUtils.isEmpty(apklocalUrl) && new File(apklocalUrl).exists()) {
            File file = new File(apklocalUrl);
            boolean exists = file.exists();
            if (exists) {
                file.delete();
            }
        }
    }

    /**
     * 强制更新
     *
     * @param version
     * @return
     */
    private void forceUpdate(final Version version) {
        //提示强制更新
        if (tipsDialogProvider != null) {
            tipsDialogProvider.getForceUpdateDialog(version, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    showDownloadDialog(version.getUrl());
                }
            }).show(fragmentManager, "forceUpdateDialog");
        }
    }

    private void showDownloadDialog(String url) {
        final ProgressDialog mpDialog = new ProgressDialog(mContext);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置风格为线性进度条
        mpDialog.setMessage("新版本下载中，请稍后");
        mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消
        mpDialog.setCanceledOnTouchOutside(false);
        mpDialog.setMax(100);
        mpDialog.show();
        //                   delLocationNewPackage(version.getUrl());
        final String apkLocationPath = getApkLocationPath(url);
        DownLoadManager.downLoadFile(url, apkLocationPath, new DownloadListener() {
            @Override
            public void onStartDownload() {
            }

            @Override
            public void onProgress(final int progress) {
                mpDialog.setProgress(progress);
            }

            @Override
            public void onFinishDownload(boolean isHasLocation) {
                Message message = handler.obtainMessage(1, apkLocationPath);
                message.sendToTarget();
            }

            @Override
            public void onFail(String errorInfo) {
            }
        });
    }

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ApkDownLoadListener apkDownLoadListener = DownloadLibrary.getApkDownLoadListener();
                if (apkDownLoadListener != null) {
                    apkDownLoadListener.downLoadSuccess((String) msg.obj);
                }
//                Utils.installApk(mContext, (String) msg.obj);
            }
        }
    };

    /**
     * 获取本地路径
     *
     * @param url
     * @return
     */
    public String getApkLocationPath(String url) {
        String[] split = url.split("/");
        int length = split.length;
        if (length <= 0) return null;
        String fileName = split[length - 1];
        return DownloadLibrary.APK_PATH + fileName;
    }
}
