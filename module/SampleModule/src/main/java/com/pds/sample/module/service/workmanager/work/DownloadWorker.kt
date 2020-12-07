package com.pds.sample.module.service.workmanager.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.IOException

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 14:51
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        for (i in 0..99) {
            try {
                downloadSynchronously("https://www.google.com")
            } catch (e: IOException) {
                return Result.failure()
            }
        }

        return Result.success()
    }

    private fun downloadSynchronously(s: String) {

    }
}