package com.pds.sample.module.service.workmanager.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pds.sample.module.service.workmanager.Constants
import kotlinx.coroutines.delay

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 10:41
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class ProgressWorker(context: Context, parameters: WorkerParameters): CoroutineWorker(context,parameters){

    companion object {
        private const val TAG ="ProgressWorker"
        private const val delayDuration = 3*1000L
    }

    override suspend fun doWork(): Result {
        Log.e(
            TAG,"input data = ${inputData.getString(Constants.KEY_IMAGE_URI)}")
        val firstUpdate = workDataOf(Constants.Progress to 0)
        val middleUpdate = workDataOf(Constants.Progress to 50)
        val lastUpdate = workDataOf(Constants.Progress to 100)
        setProgress(firstUpdate)
        // setProgressAsync(firstUpdate)
        delay(delayDuration)
        setProgress(middleUpdate)
        delay(delayDuration)
        setProgress(lastUpdate)
        // setProgressAsync(lastUpdate)
        delay(delayDuration)
        return Result.success()

    }

}