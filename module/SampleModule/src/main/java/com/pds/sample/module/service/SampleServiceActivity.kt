package com.pds.sample.module.service

import android.app.job.JobInfo
import android.app.job.JobInfo.Builder
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.sample.module.service.workmanager.WorkerManagerActivity
import com.pds.router.module.SampleGroupRouter
import com.pds.sample.R
import kotlinx.android.synthetic.main.activity_service_task.*
import java.util.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/12/7 10:29 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.TV_SERVICE_TASK)
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SampleServiceActivity : AppCompatActivity(), View.OnClickListener {

    private val jobScheduler: JobScheduler by lazy { getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }

    companion object {
        private val JOB_SERVICE_ID by lazy { UUID.randomUUID().variant() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_task)
        startJobService.setOnClickListener(this)
        startWork.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.startJobService -> {
                val jobInfoBuild = Builder(
                    JOB_SERVICE_ID,
                    ComponentName(this, SampleJobService::class.java)
                ).setExtras(buildJobServiceExtras())
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                jobScheduler.schedule(jobInfoBuild.build())
            }
            R.id.startWork -> {
                startActivity(Intent(this, WorkerManagerActivity::class.java))
            }
        }
    }

    private fun buildJobServiceExtras(): PersistableBundle {
        val persistableBundle = PersistableBundle()
        persistableBundle.putInt("type", 1)
        return persistableBundle
    }
}