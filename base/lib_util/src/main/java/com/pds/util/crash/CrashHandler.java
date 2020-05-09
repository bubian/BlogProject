package com.pds.util.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局异常捕获
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 构造方法私有化
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            Log.e("CrashHandler", "Tvos browser APK崩溃了！！！线程名称:" + thread.toString() + "  /  异常:" + ex.toString());
            ex.printStackTrace();
        }
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 重新启动程序
//            Intent intent = new Intent(App.getAppContext(), SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//            App.getAppContext().startActivity(intent);
            // 杀进程
            android.os.Process.killProcess(android.os.Process.myPid());
            // 运行System.exit()会终止JVM, 0表示正常退出，非0表示异常退出
            System.exit(1);
        }
    }

    /**
     * 获取捕获异常的信息
     *
     * @param ex
     */
    private String collectExceptionInfos(Throwable ex) {
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        ex.printStackTrace(mPrintWriter);
        ex.printStackTrace();
        Throwable mThrowable = ex.getCause();
        // 迭代栈队列把所有的异常信息写入writer中
        while (mThrowable != null) {
            mThrowable.printStackTrace(mPrintWriter);
            // 换行 每个个异常栈之间换行
            mPrintWriter.append("\r\n");
            mThrowable = mThrowable.getCause();
        }
        // 记得关闭
        mPrintWriter.close();
        return mWriter.toString();
    }

    /**
     * 获取应用包参数信息
     */
    private void collectPackageInfos(Context context) {
        try {
            // 获得包管理器
            PackageManager mPackageManager = context.getPackageManager();
            // 得到该应用的信息，即主Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
//                mPackageInfos.put(VERSION_NAME, versionName);
//                mPackageInfos.put(VERSION_CODE, versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从系统属性中提取设备硬件和版本信息
     */
    private HashMap<String, String> mDeviceInfos = new HashMap<>();
    private void collectBuildInfos() {
        // 反射机制
        Field[] mFields = Build.class.getDeclaredFields();
        // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
        for (Field field : mFields) {
            try {
                field.setAccessible(true);
                mDeviceInfos.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取系统常规设定属性
     */
    private HashMap<String, String> mSystemInfos = new HashMap<>();
    private void collectSystemInfos(Context context) {
        Field[] fields = Settings.System.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class) {
                try {
                    String value = Settings.System.getString(context.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        mSystemInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取系统安全设置信息
     */
    private HashMap<String, String> mSecureInfos = new HashMap<>();
    private void collectSecureInfos(Context context) {
        Field[] fields = Settings.Secure.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class
                    && field.getName().startsWith("WIFI_AP")) {
                try {
                    String value = Settings.Secure.getString(context.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        mSecureInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取内存信息
     */
    private String collectMemInfos() {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();

        ArrayList<String> commandLine = new ArrayList<>();
        commandLine.add("dumpsys");
        commandLine.add("meminfo");
//        commandLine.add(Integer.toString(Process.myPid()));
        try {
            java.lang.Process process = Runtime.getRuntime()
                    .exec(commandLine.toArray(new String[commandLine.size()]));
            br = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取设备参数信息
     *
     * @param context
     */
//    private void collectInfos(Context context, Throwable ex) {
//        mExceptionInfos = collectExceptionInfos(ex);
//        collectPackageInfos(context);
//        collectBuildInfos();
//        collectSystemInfos();
//        collectSecureInfos();
//        mMemInfos = collectMemInfos();
//
//        //将信息储存到一个总的Map中提供给上传动作回调
//        infos.put(EXCEPETION_INFOS_STRING, mExceptionInfos);
//        infos.put(PACKAGE_INFOS_MAP, mPackageInfos);
//        infos.put(BUILD_INFOS_MAP, mDeviceInfos);
//        infos.put(SYSTEM_INFOS_MAP, mSystemInfos);
//        infos.put(SECURE_INFOS_MAP, mSecureInfos);
//        infos.put(MEMORY_INFOS_STRING, mMemInfos);
//    }


    /**
     * 将崩溃日志信息写入本地文件
     */
//    private String saveCrashInfo2File() {
//        StringBuffer mStringBuffer = getInfosStr(mPackageInfos);
//        mStringBuffer.append(mExceptionInfos);
//        // 保存文件，设置文件名
//        String mTime = formatter.format(new Date());
//        String mFileName = "CrashLog-" + mTime + ".log";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            try {
//                File mDirectory = new File(mDirPath);
//                Log.v(TAG, mDirectory.toString());
//                if (!mDirectory.exists())
//                    mDirectory.mkdirs();
//                FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory + File.separator + mFileName);
//                mFileOutputStream.write(mStringBuffer.toString().getBytes());
//                mFileOutputStream.close();
//                return mFileName;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    /**
     * 将HashMap遍历转换成StringBuffer
     */
    @NonNull
    public static StringBuffer getInfosStr(ConcurrentHashMap<String, String> infos) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        return mStringBuffer;
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        if (true) {
            logReportToFile(ex);
        }
        return true;
    }

    private void logReportToFile(Throwable ex) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String pathFileName = "/mnt/sdcard/tvos_browser/";
            File filePath = new File(pathFileName);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String logFileName = pathFileName + getCrashLogName() + ".log";
            try {
                File logFile = new File(logFileName);
                logFile.createNewFile();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFileName)), true);
                ex.printStackTrace(pw);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private static String getCrashLogName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return format.format(new Date());
        } catch (Exception ex) {

        }
        return null;
    }
}