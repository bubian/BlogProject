package com.pds.sample.module.service.workmanager

import android.os.Build
import androidx.annotation.RequiresApi
import com.firebase.jobdispatcher.JobService

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 16:37
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class WKJobService : JobService(){
    override fun onStartJob(job: com.firebase.jobdispatcher.JobParameters): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopJob(job: com.firebase.jobdispatcher.JobParameters): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}