package com.pds.download.http;

import androidx.annotation.NonNull;

import com.pds.download.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownLoadManager {

    private static OkHttpClient downLoadHttpClient;

    static {
        OkHttpClient.Builder downLoadHttpClientBuilder = new OkHttpClient.Builder();
        downLoadHttpClient = downLoadHttpClientBuilder.connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES).build();
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param listener
     */
    public static void downLoadFile(String url, final String filePath, final DownloadListener listener) {
        downLoadHttpClient.newCall(new Request.Builder().url(url).get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFail(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                InputStream is = null;
                long currentLen = 0;
                byte[] buf = new byte[1024];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = responseBody.byteStream();
                    File file = new File(filePath);
                    long length = responseBody.contentLength();
                    Logger.e("文件大小------------->", String.valueOf(length));
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        boolean mkdirs = parentFile.mkdirs();
                    }
                    if (!file.exists()) {
                        boolean newFile = file.createNewFile();
                    } else {
                        if (file.length() == length) {//若本地存在当前下载文件，且文件大小一致（说明是同一个文件）则，无需再向磁盘写入
                            if (is != null) is.close();
                            listener.onProgress(100);
                            listener.onFinishDownload(true);
                            Logger.e("本地已有该文件------------->", "");
                            return;
                        }
                    }
                    listener.onStartDownload();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        currentLen += len;
                        int progress = (int) (currentLen * 100 / length);
                        Logger.e("下载中------------->", String.valueOf(progress));
                        listener.onProgress(progress);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    listener.onFinishDownload(false);
                } catch (Exception e) {
                    listener.onFail(e.getMessage());
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (Exception e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}
