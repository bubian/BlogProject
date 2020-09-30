package com.pds.pay.ali.task;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * @author pengdaosong
 */
public class TaskManagerImpl implements ITaskManager {

    private final ArrayList<AsyncTask> mTasks;

    public TaskManagerImpl(int capicity) {
        this.mTasks = new ArrayList<>(capicity);
    }

    public TaskManagerImpl() {
        this(4);
    }

    @Override
    public void reset() {
        for (AsyncTask task : mTasks) {
            task.cancel(true);
        }
        mTasks.clear();
    }

    @Override
    public void addTask(AsyncTask task) {
        mTasks.add(task);
    }

    @Override
    public void removeTask(AsyncTask task) {
        mTasks.remove(task);
    }
}
