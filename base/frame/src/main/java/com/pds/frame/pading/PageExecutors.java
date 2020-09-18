package com.pds.frame.pading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:59
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PageExecutors {

    public ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    public void ioThread(Runnable runnable){
        IO_EXECUTOR.execute(runnable);
    }
}
