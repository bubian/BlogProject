package com.pds.sample.module.service.workmanager.work

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.AsyncSubject

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 15:17
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class RxDownloadWorker(context: Context, params: WorkerParameters) : RxWorker(context, params) {

    override fun createWork(): Single<Result> {
        return Observable.range(0, 100)
            .flatMap { download("https://www.google.com") }
            .toList()
            .map { Result.success() }
    }

    private fun download(s: String): AsyncSubject<Int> {
        return AsyncSubject.create()
    }
}