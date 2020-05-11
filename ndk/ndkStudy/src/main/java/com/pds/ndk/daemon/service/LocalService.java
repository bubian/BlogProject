package com.pds.ndk.daemon.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.pds.ndk.R;
import com.pds.ndk.daemon.DaemonActivity;
import com.pds.ndk.daemon.aidl.DaemonAidlInterface;
import com.pds.ndk.daemon.signal.Wathcer;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private MyBinder myBinder;
    MyServiceConnection connection;
    private PendingIntent pintent;
    private int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Wathcer wathcer=new Wathcer();
        wathcer.createWatcher(Process.myUid());
        wathcer.connectToMonitor();

        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        connection = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        startTimer();
        //让该service前台运行，避免手机休眠时系统自动杀掉该服务
        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        notification(mBuilder, new Intent(this, DaemonActivity.class));
//        NotificationManager mNotificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManger.notify(startId, mBuilder.build());

        startForeground(startId, mBuilder.build());
        return START_STICKY;
    }

    public void notification(NotificationCompat.Builder mBuilder, Intent intent) {
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //设置震动方式，延迟零秒，震动一秒，延迟一秒、震动一秒
        mBuilder.setContentTitle("服务启动中");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentInfo("Info");
        mBuilder.setTicker("服务需要启动");
        pintent= PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pintent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends DaemonAidlInterface.Stub {
        @Override
        public String getProcessName() throws RemoteException {
            return "LocalService";
        }
    }

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"LocalService 服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(LocalService.this, "LocalServcie has stopped", Toast.LENGTH_SHORT);
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        }
    }

    Timer timer;
    TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "count: " + counter++);
            }
        };
    }
}
