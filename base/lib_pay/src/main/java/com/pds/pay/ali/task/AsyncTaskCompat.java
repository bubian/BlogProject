package com.pds.pay.ali.task;

import android.os.AsyncTask;

/**
 * 替代android.support.v4.os.AsyncTaskCompat(api26找不到该类)
 */
public final class AsyncTaskCompat {

    /**
     *
     */
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task,
            Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        return task;
    }

    private AsyncTaskCompat() {
    }
}
