package com.pds.sample.module.service.workmanager.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pds.sample.module.service.workmanager.Constants

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 09:19
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class UploadWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext,workerParams){

    override fun doWork(): Result {
        // 获取输入值
        val url = inputData.getString(Constants.KEY_IMAGE_URI)
        val type = inputData.getInt(Constants.TASK_TYPE,0)
        Log.e(TAG,"inputData = $inputData")
        // 创建输出数据
        val outImageUrl = "我是输出数据"
        val outputData = workDataOf(Constants.KEY_IMAGE_URI to outImageUrl)
        // 重试使用return Result.retry()
        return if (4 == type) {
            if (runAttemptCount >= 4){
                Result.Retry.success(outputData)
            }else{
                Result.retry()
            }

        } else Result.Retry.success(outputData)
    }

    companion object{
        private const val TAG = "UploadWorker"
    }
}