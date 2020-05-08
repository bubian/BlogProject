package com.pds.blog.safe.AssetsEncryp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pds.blog.app.BlogApplication;
import com.pds.util.file.FileUtils;
import com.pds.util.file.ZipUtils;
import com.pds.util.safe.DesEncryptUtil;
import com.pds.util.safe.MD5Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

/**
 * @author: pengdaosong CreateTime:  2020-04-07 15:02 Email：pengdaosong@medlinker.com Description:
 */
public class AssetsDecodeManager {

    private static final String TAG = "AssetsUnZipManager";
    private static final String RICH_SRC_FILE = "i";

    private static final Key KEY = DesEncryptUtil.getKey("blog12345678901");
    private static final String DECODE_DIR_PARENT = getAppCacheDir();

    private static final String DECODE_DIR = DECODE_DIR_PARENT + "/richEditor/";
    private static final String DECODE_FILE_PATH = DECODE_DIR + "decode.zip";
    private static final String HASH_FILE_PATH = DECODE_DIR + "md5";

    private static final int MAX_UNZIP_NUM = 3;
    private static volatile AssetsDecodeManager mAssetsUnZipManager;
    private int mDecodeNum;
    private Thread mThread;
    private static volatile boolean mIsDecodeSuccess;
    private Runnable mRunnable;

    public static AssetsDecodeManager getInstance() {
        if (null == mAssetsUnZipManager) {
            synchronized (AssetsDecodeManager.class) {
                if (null == mAssetsUnZipManager) {
                    mAssetsUnZipManager = new AssetsDecodeManager();
                }
            }
        }
        return mAssetsUnZipManager;
    }

    private static String getAppCacheDir() {
        return BlogApplication.instance().getCacheDir().getParent() + "/rich";
    }

    public static String getRichLoadPath() {
        return "file://" + DECODE_DIR + "editor.html";
    }

    public static void decode(Context app) {
        getInstance().startDecode(app);
    }

    private void startDecode(final Context app) {
        if (null != mThread && mThread.isAlive()) {
            return;
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mIsDecodeSuccess && mDecodeNum < MAX_UNZIP_NUM) {
                    Log.d(TAG, "start decode run");
                    try {
                        long startDecodeTime = System.currentTimeMillis();
                        doRun(app);
                        mIsDecodeSuccess = true;
                        Log.i(TAG, "start decode success time consume：" + (System.currentTimeMillis() - startDecodeTime));
                        if (null != mRunnable) {
                            mRunnable.run();
                        }
                    } catch (Exception e) {
                        mDecodeNum++;
                        Log.d(TAG, "start decode exception");
                        e.printStackTrace();
                    }
                }
                mAssetsUnZipManager = null;
                mRunnable = null;
            }
        });
        mThread.setName("assets decode");
        Log.d(TAG, "start decode");
        mThread.start();
    }

    private void doRun(Context app) throws Exception {
        mIsDecodeSuccess = false;
        InputStream inputStream = null;
        try {
            inputStream = app.getAssets().open(RICH_SRC_FILE);
            String md5Src = MD5Util.getFileMD5(inputStream);
            String tmpMd5 = ParseMD5File();
            if (!TextUtils.isEmpty(tmpMd5) && tmpMd5.equals(md5Src)) {
                Log.i(TAG, "decoding has been completed");
                return;
            }

            Log.d(TAG, "start decoding ");
            File rich = new File(DECODE_DIR);
            if (!rich.exists()) {
                boolean re = rich.mkdirs();
            }
            inputStream.reset();
            File decodeZip = new File(DECODE_FILE_PATH);
            Log.d(TAG, "start decode file");
            DesEncryptUtil.decryptFile(inputStream, decodeZip, KEY);
            Log.d(TAG, "start decode unzip");
            ZipUtils.unzipFile(decodeZip, DECODE_DIR_PARENT);
            Log.d(TAG, "start decode write md5 file");
            writeMD5File(md5Src);
            Log.d(TAG, "start decode delete file");
            FileUtils.deleteFile(decodeZip);
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeMD5File(String hash) {
        if (TextUtils.isEmpty(hash)) {
            return;
        }

        BufferedWriter writer = null;
        try {
            File hashFile = new File(HASH_FILE_PATH);
            if (!hashFile.exists()) {
                boolean re = hashFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(HASH_FILE_PATH);
            writer = new BufferedWriter(fileWriter);
            writer.write(hash);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private String ParseMD5File() {
        File hashFile = new File(HASH_FILE_PATH);
        if (!hashFile.exists() || !hashFile.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(hashFile);
            reader = new BufferedReader(fileReader);
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    public static boolean isDecodeComplete() {
        return mIsDecodeSuccess;
    }

    public void setRunnable(Runnable runnable) {
        mRunnable = runnable;
    }
}
