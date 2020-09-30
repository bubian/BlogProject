package com.pds.pay.ali.task;

import android.os.AsyncTask;

/**
 * Created by heaven7 on 2016/1/27.
 */
public abstract class PayAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    private final ITaskManager manager;

    public PayAsyncTask(ITaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        manager.addTask(this);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        manager.removeTask(this);
    }
}
