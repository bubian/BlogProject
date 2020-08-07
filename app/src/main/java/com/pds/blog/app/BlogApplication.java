package com.pds.blog.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import androidx.multidex.MultiDexApplication;

import com.pds.blog.BuildConfig;
import com.pds.blog.R;
import com.pds.skin.SkinManager;
import com.pds.tool.ToolApplicationManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.prototypez.appjoint.core.AppSpec;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/16 4:54 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BlogApplication extends MultiDexApplication {
    private static BlogApplication mApp;

    public static BlogApplication instance(){
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        if ((getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) != 0){
            if (BuildConfig.BUILD_TYPE.contains("debug")){
                return;
            }
            Log.e("","程序被串改");
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        if (android.os.Debug.isDebuggerConnected()){
            if (BuildConfig.BUILD_TYPE.contains("debug")){
                return;
            }
            Log.e("","程序被串改");
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        SkinManager.init(this);
        ToolApplicationManager.onCreate(this);
    }

    private boolean isRunningInEmualtor(){
        java.lang.Process process = null;
        DataOutputStream os = null;
        boolean qk = false;
        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            os = new DataOutputStream(process.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            qk = Integer.valueOf(in.readLine()) == 1;
        } catch (IOException e) {
            qk = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                if (os != null){
                    os.close();
                }
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return qk;
    }

    private boolean isModifySignature(String packageName){
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        int sig = 0;
        try {
            pi = pm.getPackageInfo(packageName,PackageManager.GET_SIGNATURES);
            Signature[] s = pi.signatures;
            sig = s[0].hashCode();
        } catch (PackageManager.NameNotFoundException e) {
            sig = 0;
            e.printStackTrace();
        }

        if (sig != 1111111111){
            Log.e("","签名不一致");
            return true;
        }
        return false;
    }

    private boolean checkCRC(){
        boolean mod = false;
        long crc = Long.parseLong(getString(R.string.crc));
        ZipFile zf;
        try {
            zf = new ZipFile(getApplicationContext().getPackageCodePath());
            ZipEntry ze = zf.getEntry("classes.dex");
            if (ze.getCrc() == crc){
                mod = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            mod = false;
        }
        return mod;
    }
}
