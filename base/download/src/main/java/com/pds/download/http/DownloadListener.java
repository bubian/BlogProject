package com.pds.download.http;

public interface DownloadListener {

    void onStartDownload();

    void onProgress(int progress);

    void onFinishDownload(boolean isHasLocation);

    void onFail(String errorInfo);
}