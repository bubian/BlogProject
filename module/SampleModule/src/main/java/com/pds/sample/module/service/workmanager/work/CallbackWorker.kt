package com.pds.sample.module.service.workmanager.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 15:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CallbackWorker(context: Context, params: WorkerParameters) :
    ListenableWorker(context, params) {

    override fun startWork(): ListenableFuture<Result> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopped() {
        super.onStopped()
    }
}