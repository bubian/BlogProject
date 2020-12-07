package com.pds.sample.module.service.workmanager.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 15:08
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CoroutineDownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    // override val coroutineContext = Dispatchers.IO

    override suspend fun doWork(): Result = coroutineScope {
        withContext(Dispatchers.IO) {
            val jobs = (0 until 100).map {
                async {
                    downloadSynchronously("https://www.google.com")
                }
            }

            // awaitAll will throw an exception if a download fails, which CoroutineWorker will treat as a failure
            jobs.awaitAll()
            Result.success()
        }
    }

    private fun downloadSynchronously(s: String): Any {
        return String()
    }
}