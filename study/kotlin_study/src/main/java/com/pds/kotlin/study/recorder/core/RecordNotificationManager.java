package com.pds.kotlin.study.recorder.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.pds.kotlin.study.R;
import com.pds.kotlin.study.recorder.RecordActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-06 14:02
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class RecordNotificationManager {

    private static RecordNotificationManager instance;
    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;
    private int mElapsedSeconds = 0;
    private Context mContext;

    private RecordService.OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public static RecordNotificationManager instance(Context context) {
        if (instance == null) {
            synchronized (AudioRecordManager.class) {
                if (instance == null) {
                    instance = new RecordNotificationManager(context);
                }
            }
        }
        return instance;
    }

    private RecordNotificationManager(Context context){
        mContext = context;
    }

    public void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
                        .setContentTitle(mContext.getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(mContext, 0,
                new Intent[]{new Intent(mContext, RecordActivity.class)}, 0));

        return mBuilder.build();
    }

    public void cancelIncrementTimerTask() {
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }
    }

    public void doNotification(Service service){
        startTimer();
        service.startForeground(1, createNotification());
    }
}
