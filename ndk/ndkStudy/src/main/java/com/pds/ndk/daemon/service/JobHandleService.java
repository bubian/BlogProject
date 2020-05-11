package com.pds.ndk.daemon.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

@SuppressLint("NewApi")
public class JobHandleService extends JobService {
    private static final String TAG = "JobHandleService";
    private static int kJobId = 0x0001;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "jobService create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "JobHandleService start");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    public JobHandleService() {
    }

    public void scheduleJob(JobInfo job) {
        Log.d(TAG, "scheduleJob");
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(job);
    }


    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId, new ComponentName(
                this, JobHandleService.class));
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setPeriodic(100);
        return builder.build();
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        boolean isLocalServiceWork = isServiceWork(this,"md.edu.pds.kt.dp.servic.LocalService");
        boolean isRemoteServiceWork = isServiceWork(this, "md.edu.pds.kt.dp.servic.RemoteService");

        if (!isLocalServiceWork || !isRemoteServiceWork) {
            this.startService(new Intent(this, LocalService.class));
            this.startService(new Intent(this, RemoteService.class));
            Toast.makeText(this, "process started by jobHandleService", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");
        scheduleJob(getJobInfo());
        return true;
    }


    public boolean isServiceWork(Context context, String serviceName) {
        boolean isWorked = false;
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(128);
        if (list.size() < 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).service.getClassName().toString();
            if (serviceName.equals(name)) {
                isWorked = true;
                break;
            }
        }
        return isWorked;
    }
}
