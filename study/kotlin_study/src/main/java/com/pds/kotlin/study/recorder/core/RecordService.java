package com.pds.kotlin.study.recorder.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RecordNotificationManager.instance(getApplicationContext()).doNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioRecordManager.instance().startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        AudioRecordManager.instance().stopRecording();
        super.onDestroy();
    }
}
