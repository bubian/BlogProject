package com.pds.sample.module.service.workmanager

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import androidx.work.*
import com.firebase.jobdispatcher.*
import com.pds.sample.R
import com.pds.sample.module.service.workmanager.data.WorkInfoData
import com.pds.sample.module.service.workmanager.work.ProgressWorker
import com.pds.sample.module.service.workmanager.work.UploadWorker
import kotlinx.android.synthetic.main.activity_worker_manager.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-21 09:24
 * Email：pengdaosong@medlinker.com
 * Description: 为了尽量不使用其它库api，所以并没有用databinding来进行数据和UI的绑定
 */
class WorkerManagerActivity: AppCompatActivity(), View.OnClickListener {

    private val workInfoData = MutableLiveData<WorkInfoData>()
    private val constraints = Constraints.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleObserver()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_manager)
        simple_task.setOnClickListener(this)
        constraints_task.setOnClickListener(this)
        process_task.setOnClickListener(this)
        link_task.setOnClickListener(this)
        repeat_task.setOnClickListener(this)
        unique_task.setOnClickListener(this)
        job_service.setOnClickListener(this)
        network_task.setOnClickListener(this)
        // 初始化数据
        workInfoData.value = WorkInfoData()
        workInfoData.observe(this, Observer {
            work_output.text = HtmlCompat.fromHtml(workInfoData.value.toString(),HtmlCompat.FROM_HTML_MODE_COMPACT)
        })

        initCheckBox()
    }

    @SuppressLint("RestrictedApi")
    private fun initCheckBox() {
        cb_Storage_not_low.setOnCheckedChangeListener {_,isChecked ->
            // 在非低电量时执行工作
            constraints.setRequiresStorageNotLow(isChecked)
        }
        cb_network_type.setOnCheckedChangeListener {_,isChecked ->
            // 执行任务需要的网络类型，比如NOT_REQUIRED表示执行该任务不需要联网
            constraints.requiredNetworkType = if(isChecked)  NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
        }
        cb_charging.setOnCheckedChangeListener {_,isChecked ->
            // 在充电量时执行工作
            constraints.setRequiresCharging(isChecked)
        }
        cb_device_idle.setOnCheckedChangeListener {_,isChecked ->
            // 在设备空闲时执行工作
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                constraints.setRequiresDeviceIdle(isChecked)
            }
        }
        cb_content_update_delay.setOnCheckedChangeListener {_,isChecked ->
            // 在内容更新延迟
            constraints.triggerContentUpdateDelay = if (isChecked) 50000 else 0
        }
        cb_max_content_update_delay.setOnCheckedChangeListener {_,isChecked ->
            // 在最大内容更新延迟
            constraints.triggerMaxContentDelay = 3* 5000
        }
    }

    private fun lifecycleObserver(){
        lifecycle.addObserver(LifecycleEventObserver{ _, _ ->
            workInfoData.value?.lifecycle = lifecycle.currentState
        })
    }

    private fun cancelWorkById(requestWorker: CoroutineWorker){
        // 撤销工作
        WorkManager.getInstance(this).cancelWorkById(requestWorker.id)
    }

    // 观察工作的状态
    private fun observingWorkStates(workRequest: WorkRequest){
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this, Observer {workInfo->
            workInfoData.value?.workInfo = workInfo
            workInfoData.value?.lifecycle = lifecycle.currentState
            workInfoData.postValue(workInfoData.value)
        })
    }

    private var originName = ""
    private var lastSlectedView: Button? = null

    override fun onClick(v: View) {
        WorkManager.getInstance(this).cancelAllWork()
        if (v.id == lastSlectedView?.id){
            if (lastSlectedView?.text == "停止任务"){
                lastSlectedView?.text = originName
                lastSlectedView?.setBackgroundColor(resources.getColor(R.color.aJetpack_green_500))
                lastSlectedView?.setTextColor(resources.getColor(R.color.aJetpack_yellow_500))
                return
            }
        }else{
            lastSlectedView?.text = originName
            lastSlectedView?.setBackgroundColor(resources.getColor(R.color.aJetpack_green_500))
            lastSlectedView?.setTextColor(resources.getColor(R.color.aJetpack_yellow_500))
        }
        lastSlectedView = v as Button
        originName = lastSlectedView?.text.toString()
        lastSlectedView?.text = "停止任务"

        constraints1.visibility = View.GONE
        constraints2.visibility = View.GONE
        work_output.text = ""
        when(v.id){
            R.id.simple_task -> doWorker()   // 简单任务
            R.id.constraints_task ->{
                constraints1.visibility = View.VISIBLE
                constraints2.visibility = View.VISIBLE
                // 约束任务
                doConstraintsWorker(constraints)
            }
            R.id.process_task -> progressWork()
            R.id.link_task -> chainingWork()
            R.id.repeat_task -> recurringWork()
            R.id.unique_task -> uniqueWork()
            R.id.job_service -> jobServiceWork()
            R.id.network_task -> gCMNetworkToWM()
        }

        lastSlectedView?.setBackgroundColor(Color.RED)
        lastSlectedView?.setTextColor(Color.WHITE)
    }

    @SuppressLint("RestrictedApi")
    private fun doWorker() {
        workInfoData.value?.name = "简单任务"
        // 创建任务请求
        val request = OneTimeWorkRequestBuilder<UploadWorker>().build()
        observingWorkStates(request)
        workInfoData.value?.constraint = request.workSpec?.constraints
        // 将任务加入队列
        WorkManager.getInstance(this).enqueue(request)

    }

    private fun doConstraintsWorker(constraints: Constraints) {
        // 定义任务输入输出数据
        val inputData = workDataOf(Constants.KEY_IMAGE_URI to "https://github.com/android/plaid")
        // 创建任务请求
        val request =
            OneTimeWorkRequestBuilder<UploadWorker>()
                //设置约束条件
                .setConstraints(constraints)
                // 延迟运行任务
                .setInitialDelay(10,TimeUnit.MINUTES)
                // 重试策略
                .setBackoffCriteria(BackoffPolicy.LINEAR,OneTimeWorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
                // 设置任务输入数据
                .setInputData(inputData)
                // 设置标签，将任务分组，方便后面的根据组别操作
                .addTag("cleanup")
                // 尝试合并输入，并在必要时创建数组
                .setInputMerger(ArrayCreatingInputMerger::class)
                .build()
        observingWorkStates(request)
        // 将任务加入队列
        val operation = WorkManager.getInstance(this).enqueue(request)
        // 获取结果
        val result = operation.result
    }

    // 获取进度
    private fun progressWork(){
        workInfoData.value?.name = "进度任务"
        val progress = OneTimeWorkRequestBuilder<ProgressWorker>().build()
        observingWorkStates(progress)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(progress.id).observe(this, Observer {wi->
            if (wi != null) {
                workInfoData.value?.workInfo = wi
                workInfoData.postValue(workInfoData.value)
            }
        })
        WorkManager.getInstance(this).enqueue(progress)
    }
    // 工作链
    private fun chainingWork(){
        val one = OneTimeWorkRequestBuilder<UploadWorker>().build()
        val two = OneTimeWorkRequestBuilder<UploadWorker>().build()
        val request = OneTimeWorkRequestBuilder<UploadWorker>().build()
        val progress = OneTimeWorkRequestBuilder<ProgressWorker>().build()

        WorkManager.getInstance(this).beginWith(listOf(one,two)).then(request).then(progress).enqueue()
    }

    // 重复工作
    private fun recurringWork(){
        workInfoData.value?.name = "重复工作任务"
        val inputData = workDataOf(Constants.TASK_TYPE to 4)
        /**
         * MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L
         * The minimum interval duration for {@link PeriodicWorkRequest} (in milliseconds).
         */

        /** MIN_PERIODIC_FLEX_MILLIS = 5 * 60 * 1000L; // 5 minutes.
         * The minimum flex duration for {@link PeriodicWorkRequest} (in milliseconds).
         */
        // 这里有最短的重复执行时间

        val uploadRequest =
            PeriodicWorkRequestBuilder<UploadWorker>(3, TimeUnit.SECONDS)
                .setInputData(inputData)
                .build()

        observingWorkStates(uploadRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {wi->
            if (wi != null) {
                workInfoData.value?.workInfo = wi
                workInfoData.postValue(workInfoData.value)
            }
        })
        WorkManager.getInstance(this).enqueue(uploadRequest)
    }

    // 唯一工作
    // 当有相同的key的任务添加时，会停止之前所以该类key的任务
    private fun uniqueWork(){
        workInfoData.value?.name = "唯一工作任务"
        val one = OneTimeWorkRequestBuilder<UploadWorker>().build()
        val two = OneTimeWorkRequestBuilder<UploadWorker>().build()
        WorkManager.getInstance(this).beginUniqueWork(Constants.UNIQUE_WORK, ExistingWorkPolicy.APPEND,listOf(one,two)).enqueue()
    }

    //初始化WorkManager配置
    // 更多参考官网：https://developer.android.google.cn/topic/libraries/architecture/workmanager/advanced/custom-configuration?hl=en
    private fun initConfiguration(){
        val myConfig = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO).setExecutor(Executors.newFixedThreadPool(8))
            .build()
        WorkManager.initialize(this, myConfig)
    }

    // 使用JobService
    private fun jobServiceWork(){
        workInfoData.value?.name = "Job Service任务"
        val input: Bundle = Bundle().apply {
            putString("some_key", "some_value")
        }

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val job = dispatcher.newJobBuilder()
            .setService(WKJobService::class.java)
            .setRecurring(false)
            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
            .setTrigger(Trigger.executionWindow(0,6))
            .setReplaceCurrent(false)
            .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).setConstraints(
                Constraint.ON_UNMETERED_NETWORK,
                Constraint.DEVICE_CHARGING
            )
            .setExtras(input)
            .setTag("my-unique-tag")
            .build()

        dispatcher.mustSchedule(job)
    }

    private fun gCMNetworkToWM(){
        workInfoData.value?.name = "CMNetwork任务"
//        val myTask = OneoffTask.Builder()
//            .setService(WMGcmTaskService::class.java)
//            .setRequiresCharging(true)
//            .setExecutionWindow(5 * 20, 15 * 40)
//            .setTag("test-upload")
//            .build()
//        GcmNetworkManager.getInstance(this).schedule(myTask)
    }
}