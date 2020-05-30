package com.pds.edit.task;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.pds.edit.core.util.BitmapLoadUtils;
import com.pds.edit.core.util.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class BitmapLoadTask extends AsyncTask<Void, Void, BitmapLoadTask.BitmapWorkerResult> {

    private static final String TAG = "BitmapWorkerTask";

    private final Context mContext;
    private Uri mInputUri;
    private Uri mOutputUri;
    private final int mRequiredWidth;
    private final int mRequiredHeight;

    private final BitmapLoadCallback mBitmapLoadCallback;
    private String mImageCachePath;

    public static class BitmapWorkerResult {

        Bitmap mBitmapResult;
        ExifInfo mExifInfo;
        Exception mBitmapWorkerException;

        public BitmapWorkerResult(@NonNull Bitmap bitmapResult, @NonNull ExifInfo exifInfo) {
            mBitmapResult = bitmapResult;
            mExifInfo = exifInfo;
        }

        public BitmapWorkerResult(@NonNull Exception bitmapWorkerException) {
            mBitmapWorkerException = bitmapWorkerException;
        }

    }

    public BitmapLoadTask(@NonNull Context context,
                          @NonNull Uri inputUri, @Nullable Uri outputUri,
                          int requiredWidth, int requiredHeight,
                          BitmapLoadCallback loadCallback) {
        mContext = context;
        mInputUri = inputUri;
        mOutputUri = outputUri;
        mRequiredWidth = requiredWidth;
        mRequiredHeight = requiredHeight;
        mBitmapLoadCallback = loadCallback;
//        mImageCachePath = mContext.getCacheDir().getAbsolutePath() + "image_editors/";

    }

    @Override
    @NonNull
    protected BitmapWorkerResult doInBackground(Void... params) {
        if (mInputUri == null) {
            return new BitmapWorkerResult(new NullPointerException("Input Uri cannot be null"));
        }

        try {
            processInputUri();
        } catch (NullPointerException | IOException e) {
            return new BitmapWorkerResult(e);
        }

        String inputUriScheme = mInputUri.getScheme();
        FileInputStream fileInputStream = null;
        FileDescriptor fileDescriptor = null;
        if ("http".equals(inputUriScheme) || "https".equals(inputUriScheme)) {
            try {
                fileInputStream = new FileInputStream(new File(mOutputUri.toString()));
                if (fileInputStream != null) {
                    fileDescriptor = fileInputStream.getFD();
                } else {
                    return new BitmapWorkerResult(new NullPointerException("ParcelFileDescriptor was null for given Uri: [" + mInputUri + "]"));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            final ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(mInputUri, "r");
            } catch (FileNotFoundException e) {
                return new BitmapWorkerResult(e);
            }
            if (parcelFileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            } else {
                return new BitmapWorkerResult(new NullPointerException("ParcelFileDescriptor was null for given Uri: [" + mInputUri + "]"));
            }
        }


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            return new BitmapWorkerResult(new IllegalArgumentException("Bounds for bitmap could not be retrieved from the Uri: [" + mInputUri + "]"));
        }

        options.inSampleSize = BitmapLoadUtils.calculateInSampleSize(options, mRequiredWidth, mRequiredHeight);
        options.inJustDecodeBounds = false;

        Bitmap decodeSampledBitmap = null;

        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            try {
                decodeSampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                decodeAttemptSuccess = true;
            } catch (OutOfMemoryError error) {
                Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                options.inSampleSize *= 2;
            }
        }

        if (decodeSampledBitmap == null) {
            return new BitmapWorkerResult(new IllegalArgumentException("Bitmap could not be decoded from the Uri: [" + mInputUri + "]"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            BitmapLoadUtils.close(fileInputStream);
        }

        int exifOrientation = BitmapLoadUtils.getExifOrientation(mContext, mInputUri);
        int exifDegrees = BitmapLoadUtils.exifToDegrees(exifOrientation);
        int exifTranslation = BitmapLoadUtils.exifToTranslation(exifOrientation);

        ExifInfo exifInfo = new ExifInfo(exifOrientation, exifDegrees, exifTranslation);

        Matrix matrix = new Matrix();
        if (exifDegrees != 0) {
            matrix.preRotate(exifDegrees);
        }
        if (exifTranslation != 1) {
            matrix.postScale(exifTranslation, 1);
        }
        if (!matrix.isIdentity()) {
            return new BitmapWorkerResult(BitmapLoadUtils.transformBitmap(decodeSampledBitmap, matrix), exifInfo);
        }

        return new BitmapWorkerResult(decodeSampledBitmap, exifInfo);
    }

    private void processInputUri() throws NullPointerException, IOException {
        String inputUriScheme = mInputUri.getScheme();
        Log.d(TAG, "Uri scheme: " + inputUriScheme);
        if ("http".equals(inputUriScheme) || "https".equals(inputUriScheme)) {
            try {
                downloadFile(mInputUri, mOutputUri);
            } catch (NullPointerException | IOException e) {
                Log.e(TAG, "Downloading failed", e);
                throw e;
            }
        } else if ("content".equals(inputUriScheme)) {
            String path = getFilePath();
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                mInputUri = Uri.fromFile(new File(path));
            } else {
                try {
                    copyFile(mInputUri, mOutputUri);
                } catch (NullPointerException | IOException e) {
                    Log.e(TAG, "Copying failed", e);
                    throw e;
                }
            }
        } else if (!"file".equals(inputUriScheme)) {
            Log.e(TAG, "Invalid Uri scheme " + inputUriScheme);
            throw new IllegalArgumentException("Invalid Uri scheme" + inputUriScheme);
        }
    }

    private String getFilePath() {
        if (ContextCompat.checkSelfPermission(mContext, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return FileUtils.getPath(mContext, mInputUri);
        } else {
            return null;
        }
    }

    private void copyFile(@NonNull Uri inputUri, @Nullable Uri outputUri) throws NullPointerException, IOException {
        Log.d(TAG, "copyFile");

        if (outputUri == null) {
            throw new NullPointerException("Output Uri is null - cannot copy image");
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = mContext.getContentResolver().openInputStream(inputUri);
            outputStream = new FileOutputStream(new File(outputUri.getPath()));
            if (inputStream == null) {
                throw new NullPointerException("InputStream for given input Uri is null");
            }

            byte buffer[] = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            BitmapLoadUtils.close(outputStream);
            BitmapLoadUtils.close(inputStream);
//            mInputUri = mOutputUri;
        }
    }

    private void downloadFile(@NonNull Uri inputUri, @Nullable Uri outputUri) throws NullPointerException, IOException {
        Log.d(TAG, "downloadFile");

        if (outputUri == null) {
            throw new NullPointerException("Output Uri is null - cannot download image");
        }

        String inputUriStr = inputUri.toString();
//        if (!isPictureFormat(inputUriStr)){
//            throw new IllegalArgumentException("download file is not picture format,url = "+inputUriStr);
//        }

        FileOutputStream fos = null;
        try{
            URL url = new URL(inputUriStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //设置客户端连接超时间隔，如果超过指定时间  服务端还没有响应 就不要等了
            httpURLConnection.setReadTimeout(6*1000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(3*1000);
            //判断服务端正常的反馈是否已经到达了 客户端
            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //获得网络字节输入流对象
                InputStream is =httpURLConnection.getInputStream();
                //建立内存到硬盘的连接
                String pictureName = getPictureName(inputUriStr);
                File file = new File(mOutputUri.toString(),pictureName);
                if (!file.exists()) file.createNewFile();

                mOutputUri = Uri.parse(file.getAbsolutePath());
                fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int len;
                while((len=is.read(b))!=-1){  //先读到内存
                    fos.write(b, 0, len);
                }
                fos.flush();
                Log.d(TAG,"下载成功");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        mInputUri = mOutputUri;
    }

    private String getPictureName(String url) {
        int lastSplitIndex = url.lastIndexOf("/");
        return url.substring(lastSplitIndex,url.length());
    }

    private boolean isPictureFormat(@NonNull String fileName){
        return fileName.endsWith(".png") || fileName.endsWith(".jpg");
    }

    @Override
    protected void onPostExecute(@NonNull BitmapWorkerResult result) {
        if (result.mBitmapWorkerException == null) {
            mBitmapLoadCallback.onBitmapLoaded(result.mBitmapResult, result.mExifInfo, mInputUri.getPath(), (mOutputUri == null) ? null : mOutputUri.getPath());
        } else {
            mBitmapLoadCallback.onFailure(result.mBitmapWorkerException);
        }
    }

}
