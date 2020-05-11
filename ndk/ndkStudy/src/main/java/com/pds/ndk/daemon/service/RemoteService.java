package com.pds.ndk.daemon.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.pds.ndk.R;
import com.pds.ndk.daemon.DaemonActivity;
import com.pds.ndk.daemon.aidl.DaemonAidlInterface;

public class RemoteService extends Service {
    private static final String TAG = "RemoteService";
    private MyRemoteService binder;
    private MyRemoteServiceConnection connection;
    private PendingIntent pintent;
    public RemoteService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null) {
            binder = new MyRemoteService();
        }
        connection = new MyRemoteServiceConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //连接本地服务
        this.bindService(new Intent(this, LocalService.class),connection, Context.BIND_IMPORTANT);

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
    class MyRemoteService extends DaemonAidlInterface.Stub {

        @Override
        public String getProcessName() throws RemoteException {
            return "RemoteService";
        }
    }

    class MyRemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "RemoteService 服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //LocalService 被干掉了
            Toast.makeText(RemoteService.this, "RemoteService 服务连接被干掉", Toast.LENGTH_SHORT).show();
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), connection, Context.BIND_IMPORTANT);
        }
    }
}
