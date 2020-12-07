package com.pds.sample.module.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.annotation.RequiresApi
import com.pds.web.util.LogUtil

/**
 * @author: pengdaosong
 * @CreateTime:  2020/12/7 10:05 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SampleJobService : JobService(){
   companion object{
       private  const val TAG = "SampleJobService"
   }

    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG,"onCreate")
    }

    /**
     * return: true -> 任务还没有执行完毕，false -> 任务执行完毕
     */
    override fun onStartJob(params: JobParameters): Boolean {
        LogUtil.d(TAG,"start:params=${params.extras.getInt("type")}")
        return true
    }

    /**
     * return: true -> 任务将被再次调度执行，false -> 任务完全结束
     */
    override fun onStopJob(params: JobParameters): Boolean {
        LogUtil.d(TAG,"onStopJob:params=${params.extras.getInt("type")}")
        return true
    }

    /**
     * 当onStartJob返回true，表示任务还没有完成，不会调用onStopJob，任务完成可以手动调用jobFinished方法，表示任务执行完成
     */
    private fun finish(params: JobParameters, wantsReschedule : Boolean) {
        jobFinished(params,wantsReschedule)
    }
}