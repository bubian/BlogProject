package com.pds.util.download;

import android.os.Environment;
import android.util.Log;

import com.pds.util.file.FileUtils1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class DownLoadUtils {
    private static final int DOWNLOAD_BUFFER_SIZE = 1024 * 256;
    /**
     * 下载文件
     *
     * @param serverUrl
     * @param version          meta中的baseVersion
     * @param downloadFileName 要下载的文件名
     * @param savePath         保存文件的路径
     * @param progressCallback 下载进度回调
     * @return 是否下载成功
     */
    public static boolean download(String serverUrl, String version, String downloadFileName, String savePath,
                                   DownloadProgressCallback progressCallback) throws IOException {
        String downloadUrlString = String.format("%s/%s/%s", serverUrl, version, downloadFileName);
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        File downloadFile = null;
        boolean isZip = false;
        boolean isDownLoadSuccess = false;

        try {
            URL downloadUrl = new URL(downloadUrlString);
            connection = (HttpURLConnection) (downloadUrl.openConnection());
            connection.setRequestProperty("Accept-Encoding", "identity");
            bin = new BufferedInputStream(connection.getInputStream());

            long totalBytes = connection.getContentLength();
            long receivedBytes = 0;
            File downloadFolder = new File(savePath);
            downloadFolder.mkdirs();

            downloadFile = new File(downloadFolder, downloadFileName);
            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            byte[] header = new byte[4];

            int numBytesRead;
            while ((numBytesRead = bin.read(data, 0, DOWNLOAD_BUFFER_SIZE)) >= 0) {
                if (receivedBytes < 4) {
                    for (int i = 0; i < numBytesRead; i++) {
                        int headerOffset = (int) (receivedBytes) + i;
                        if (headerOffset >= 4) {
                            break;
                        }

                        header[headerOffset] = data[i];
                    }
                }

                receivedBytes += numBytesRead;
                bout.write(data, 0, numBytesRead);
                if (progressCallback != null) {
                    progressCallback.call(new DownloadProgress(totalBytes, receivedBytes));
                }
            }

            if (totalBytes != receivedBytes) {
                throw new RuntimeException("Received " + receivedBytes + " bytes, expected " + totalBytes);
            }

            isZip = ByteBuffer.wrap(header).getInt() == 0x504b0304;
            isDownLoadSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("Error closing IO resources.", e);
            }
        }

        if (isZip) {
            FileUtils1.unzipFile(downloadFile, savePath);
            FileUtils1.deleteFileOrFolderSilently(downloadFile);
        }
        return isDownLoadSuccess;
    }

    /**
     * 下载差分包
     * @param url
     * @return
     * @throws Exception
     */
    public static File download(String url, String localPath){
        File file = null;
        InputStream is = null;
        FileOutputStream os = null;
        try {
            file = new File(Environment.getExternalStorageDirectory(),localPath);
            if (file.exists()) {
                file.delete();
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            is = conn.getInputStream();
            os = new FileOutputStream(file);
            byte[] buffer = new byte[1*1024];
            int len = 0;
            while((len = is.read(buffer)) != -1){
                Log.d("Tim", String.valueOf(len));
                os.write(buffer, 0, len);
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
