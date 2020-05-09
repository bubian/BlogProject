package com.pds.util.adb;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by MrPds
 * Data 2016.7.19
 * func 执行adb命令
 */

public class ExecuteAdbCommand {
    private static final String TAG = ExecuteAdbCommand.class.getSimpleName();

    public static void runAdbCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process pc = runtime.exec(command);
        try {
            if (pc.waitFor() != 0) {
                Log.e(TAG,"exit value = " + pc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line+" ");
            }
            Log.i(TAG,stringBuffer.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            try {
                pc.destroy();
            } catch (Exception e2) {
            }
        }
    }

    public void execShell(String cmd){
        try{
            //权限设置
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
}
