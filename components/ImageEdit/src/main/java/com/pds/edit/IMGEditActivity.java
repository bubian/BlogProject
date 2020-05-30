package com.pds.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.IMGText;
import com.pds.edit.core.file.IMGAssetFileDecoder;
import com.pds.edit.core.file.IMGDecoder;
import com.pds.edit.core.file.IMGFileDecoder;
import com.pds.edit.core.util.BitmapLoadUtils;
import com.pds.edit.core.util.IMGUtils;
import com.pds.edit.task.BitmapLoadCallback;
import com.pds.edit.task.ExifInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class IMGEditActivity extends IMGEditBaseActivity {

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;

    public static final String EXTRA_INPUT_IMAGE_URI = "INPUT_IMAGE_URI";
    public static final String EXTRA_IMAGE_SAVE_PATH = "IMAGE_SAVE_PATH";
    public static final String EXTRA_EXT_IMAGE_SAVE_PATH = "EXT_IMAGE_SAVE_PATH";


    private int mMaxBitmapSize = 0;
    private String mImageInputUrl;

    private int originImageWidth;
    private int originImageHeight;
    private static boolean isStartActivity = true;

    public static void startActivity(Activity activity,Uri url ,int requestCode){
        if (!isStartActivity)return;
        isStartActivity = false;
        Intent editActivity = new Intent(activity,IMGEditActivity.class);
        editActivity.putExtra(IMGEditActivity.EXTRA_INPUT_IMAGE_URI, url);
        activity.startActivityForResult(editActivity, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStartActivity = true;
    }

    public void fetchBitmap(@NonNull Uri inputUri, @Nullable Uri outputUri) {

        int maxBitmapSize = getMaxBitmapSize();
        BitmapLoadUtils.decodeBitmapInBackground(this,inputUri,outputUri,maxBitmapSize,maxBitmapSize,new BitmapLoadCallback(){
            @Override
            public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                if (null == bitmap){
                    finish();
                }
                originImageWidth = bitmap.getWidth();
                originImageHeight = bitmap.getHeight();
                mImgView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(@NonNull Exception bitmapWorkerException) {
                bitmapWorkerException.printStackTrace();
            }
        });
    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(this);
        }
        return mMaxBitmapSize;
    }

    @Override
    protected void doFetchImage(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent == null) return;
        Uri inputUri = intent.getParcelableExtra(EXTRA_INPUT_IMAGE_URI);
        mImageInputUrl = inputUri.toString();
        Uri outPutUrl = Uri.parse(getCacheDir().getAbsolutePath());
        if (inputUri == null) return ;
        fetchBitmap(inputUri,outPutUrl);
    }

    @Override
    public Bitmap getBitmap() {
        Intent intent = getIntent();
        if (intent == null) return null;
        Uri uri = intent.getParcelableExtra(EXTRA_INPUT_IMAGE_URI);
        if (uri == null) return null;
        IMGDecoder decoder = null;
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            switch (uri.getScheme()) {
                case "asset":
                    decoder = new IMGAssetFileDecoder(this, uri);
                    break;
                case "file":
                    decoder = new IMGFileDecoder(uri);
                    break;
            }
        }
        if (decoder == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        decoder.decode(options);

        if (options.outWidth > MAX_WIDTH) options.inSampleSize = IMGUtils.inSampleSize(Math.round(1f * options.outWidth / MAX_WIDTH));
        if (options.outHeight > MAX_HEIGHT) options.inSampleSize = Math.max(options.inSampleSize, IMGUtils.inSampleSize(Math.round(1f * options.outHeight / MAX_HEIGHT)));

        options.inJustDecodeBounds = false;
        Bitmap bitmap = decoder.decode(options);
        if (bitmap == null) return null;
        return bitmap;
    }

    @Override
    public void onText(IMGText text) {
        mImgView.addStickerText(text);
    }

    @Override
    public void onModeClick(IMGMode mode) {
        IMGMode cm = mImgView.getMode();
        if (cm == mode) {
            mode = IMGMode.NONE;
        }
        mImgView.setMode(mode);
    }

    @Override
    public void onCancelClick() {
        finish();
    }

    @Override
    public void onDoneClick() {
        boolean isEditor = mImgView.isEditor();

        String path = getFilesDir().getAbsolutePath();

        String savePath = path + UUID.randomUUID().toString() + ".jpg";
        String extSavePath = path + UUID.randomUUID().toString() + ".jpg";

        Bitmap bitmapAll = mImgView.saveBitmap(false);

        boolean isClip = bitmapAll.getWidth() != originImageWidth || bitmapAll.getHeight() != originImageHeight;

        if(!isEditor && !isClip && !mImgView.isEditorMosaic()){
            setResult(RESULT_CANCELED);
            finish();
        }

        Intent intent = new Intent();

        if (isClip || mImgView.isEditorMosaic()){
            bitmapToImage(mImgView.saveBitmap(true),extSavePath);
        }

        if (isEditor){
            bitmapToImage(bitmapAll,savePath);
        }


        File markImageFile = new File(savePath);
        File extImageFile = new File(extSavePath);

        if (!markImageFile.exists() && !extImageFile.exists()){
            setResult(RESULT_CANCELED,intent);
            finish();
            return;
        }


        if (markImageFile.exists()){
            intent.putExtra(EXTRA_IMAGE_SAVE_PATH,savePath);
        }

        if (extImageFile.exists()){
            intent.putExtra(EXTRA_EXT_IMAGE_SAVE_PATH,extSavePath);
        }

        intent.putExtra(EXTRA_INPUT_IMAGE_URI,mImageInputUrl);

        setResult(RESULT_OK,intent);
        finish();

    }

    private String bitmapToImage(Bitmap bitmap,String savePath){
        if (bitmap != null) {
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(savePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return savePath;
    }

    @Override
    public void onCancelClipClick() {
        showBottomNavigation();
        clipViewLayout.setVisibility(View.GONE);
        mImgView.cancelClip();
        IMGMode mode = mImgView.getMode();
        bottomNavigation.resetLastSelect(getBottomNavigationId(mode));
        updateModeUI();
    }
    @Override
    public void onDoneClipClick() {
        showBottomNavigation();
        clipViewLayout.setVisibility(View.GONE);
        mImgView.doClip();
        IMGMode mode = mImgView.getMode();
        bottomNavigation.resetLastSelect(getBottomNavigationId(mode));
        updateModeUI();
    }



    @Override
    public void onResetClipClick() {
        mImgView.resetClip();
    }

    @Override
    public void onRotateClipClick() {
        mImgView.doRotate();
    }


    @Override
    public void onClick(View v) {

    }
}
