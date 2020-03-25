package com.pds.util.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cn.glidelib.glide.GlideApp;
import com.medlinker.base.entity.FileEntity;
import com.medlinker.base.utils.DimenUtil;
import com.medlinker.base.utils.ToastUtil;

import net.medlinker.medlinker.BuildConfig;
import net.medlinker.medlinker.R;
import net.medlinker.medlinker.app.MedlinkerApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 对图片进行压缩处理
 * @time 2015/11/2 11:10
 */
public class PictureUtil {

    public static final String LOCAL_FILE_NAME = "Medlinker";

    private static final String SPLASH_FILE_NAME = "splash.jpg";

    /**
     * 把bitmap转换成String
     *
     * @param filePath
     * @return
     */
    public static byte[] bitmapToByte(String filePath) {
        int or = readPictureDegree(filePath); // 旋转的度数
        Bitmap bm = getSmallBitmap(filePath);
        if (null == bm) {
            return null;
        }
        bm = rotaingImageView(or, bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    public static byte[] bitmapToByte(Bitmap bm) {
        if (null == bm) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    /**
     * 把原图bitmap转换成byte
     *
     * @param filePath
     * @return
     */
    public static byte[] originalBitmapToByte(String filePath) {
        FileInputStream input = null;
        int or = readPictureDegree(filePath); // 旋转的度数
        try {
            input = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(input);
        if (or != 0) {
            bmp = rotaingImageView(or, bmp);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取缩放后的bitmap
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public static void getHW(String filePath, int[] xy) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        xy[0] = options.outWidth;
        xy[1] = options.outHeight;
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 保存文件
     *
     * @param bitmap
     * @return
     */
    public static String saveImgToLocal(Bitmap bitmap, String filePath) {
        if (null == bitmap) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file.canWrite()) {
            return null;
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 把一个bitmap写入到本地sdcard临时文件夹.
     *
     * @param bitmap
     */
    public static String saveImgToTemp(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        String fileName = CommonUtil.getMyUUID().concat(".jpg");
        String localPath = getTempFilePath() + fileName;
        return saveImgToLocal(bitmap, localPath);
    }


    /**
     * 把一个bitmap写入到本地sdcard.
     *
     * @param bitmap
     */
    public static String saveImgToLocal(Context context, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        String fileName = CommonUtil.getMyUUID() + ".jpg";
        String localPath = getPictureFilePath() + fileName;
        File file = new File(localPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            //通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.concat(".provider"), file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
            } else {
                intent.setData(Uri.fromFile(file));
            }
            context.sendBroadcast(intent);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveImgToLocal(final Context context, final View v, final boolean isShowToast) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            return;
        }

        AsyncJobUtil.doInBackground(new AsyncJobUtil.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                v.setDrawingCacheEnabled(true);
                Bitmap bitmap = v.getDrawingCache();

                final String fileName = PictureUtil.saveImgToLocal(context, bitmap);
                v.setDrawingCacheEnabled(false);
                v.destroyDrawingCache();

                // Send the result to the UI thread and show it on a Toast
                AsyncJobUtil.doOnMainThread(new AsyncJobUtil.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        if (isShowToast && !TextUtils.isEmpty(fileName)) {
                            ToastUtil.showMessage(context, context.getString(R.string.image_save_addr) + FileUtil.Constants.DEFAULT_IMAGES_DIR + "/" + fileName);
                        }
                    }
                });
            }
        });
    }

    public static void downLoadImg(final Context context, final String url, final boolean isShowToast) {
        if (url.startsWith("http:") || url.startsWith("https:")) {
            GlideApp.with(context)
                    .downloadOnly()
                    .load(url)
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(File resource, Transition<? super File> transition) {
                            if (resource != null && resource.exists()) {
                                String fileName = CommonUtil.getMyUUID() + ".jpg";
                                String localPath = getPictureFilePath() + fileName;
                                File dstFile = new File(localPath);
                                if (!dstFile.exists()) {
                                    dstFile.getParentFile().mkdirs();
                                    try {
                                        dstFile.createNewFile();
                                        FileUtil.copyFile(resource, dstFile);
                                        String path = getPictureFilePath() + "/" + fileName;
                                        sendBraodCastToGallary(context, path);
                                        if (isShowToast)
                                            ToastUtil.showMessage(context, context.getString(R.string.image_save_addr) + FileUtil.Constants.DEFAULT_IMAGES_DIR + "/" + fileName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (isShowToast) {
                                ToastUtil.showMessage(context, R.string.network_failed_please_retry);
                            }
                        }
                    });
        } else {
            if (isShowToast) {
                ToastUtil.showMessage(context, context.getString(R.string.image_save_already_exist) + url);
            }
        }
    }

    /**
     * 将广告图片存入本地
     *
     * @param bitmap
     */
    public static void saveSplashImgToLocal(Context context, Bitmap bitmap) {
        //File file = new File(localPath);
        File fileDir = context.getCacheDir();
        File file = new File(fileDir, SPLASH_FILE_NAME);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断广告图片是否存在
     *
     * @return
     */
    public static String getSplashImgFilePath(Context context) {
        File fileDir = context.getCacheDir();
        File file = new File(fileDir, SPLASH_FILE_NAME);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        return "";
    }


    /**
     * 图片保存路径.
     */
    public static String getPictureFilePath() {
        return getPictureFile().getPath() + File.separator;
    }

    /**
     * 图片保存
     */
    public static File getPictureFile() {
//        File dir = new File(ROOT_PATH);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        return dir;
        return FileUtil.getImagesDir();
    }

    /**
     * 临时文件夹路径
     *
     * @return
     */
    public static String getTempFilePath() {
        return getTempFile().getPath() + File.separator;
    }

    /**
     * 临时文件
     *
     * @return
     */
    public static File getTempFile() {
        return FileUtil.getTempDir();
    }

    /**
     * 原图加圈圈
     *
     * @param bitmap
     * @return
     */
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
        int radius = bitmap.getWidth() / 2;
        int borderWidth = DimenUtil.dip2px(12);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        Paint mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStrokeWidth(borderWidth);
        c.drawCircle(radius, radius, radius - borderWidth, paint);
        c.drawCircle(radius, radius, radius - (borderWidth * 0.5f), mBorderPaint);
        return dest;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 获取文件的大小
     *
     * @param fileEntity 本地文件路径或者网络地址
     * @return
     */
    public static String getFileSize(FileEntity fileEntity) {
        double result = 0;
        String filePathOrUrl = fileEntity.getFileUrl();
        File file = new File(filePathOrUrl);
        if (file.exists()) {
            result = file.length() / 1024.00;
        }
        if (result < 1) {
            return formatDouble2(result) + "k";
        }

        if (result < 1000) {
            return (int) result + "k";
        }
        result = result / 1024;
        return formatDouble2(result) + "M";
    }

    /**
     * NumberFormat is the abstract base class for all number formats.
     * This class provides the interface for formatting and parsing numbers.
     *
     * @param d
     * @return
     */
    public static String formatDouble2(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }

    /**
     * 本地图片改变后 通知系统相册更新
     *
     * @param context
     * @param filePath
     */
    public static void sendBraodCastToGallary(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        if (file != null && file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.concat(".provider"), file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
            } else {
                intent.setData(Uri.fromFile(file));
            }
            context.sendBroadcast(intent);
        }

        MediaScannerConnection.scanFile(MedlinkerApp.getApplication(), new String[]{filePath}, new String[]{"image/jpeg"}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(final String path, final Uri uri) {
                //your file has been scanned!
            }
        });
    }
}
