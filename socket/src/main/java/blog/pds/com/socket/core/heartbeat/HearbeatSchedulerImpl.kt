package blog.pds.com.socket.core.heartbeat

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import blog.pds.com.socket.core.dispatch.MedImReceiver
import blog.pds.com.socket.utils.NetworkUtil
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import blog.pds.com.socket.core.heartbeat.IHeartbeatScheduler.Companion as IHeartbeatScheduler1

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class HearbeatSchedulerImpl : IHeartbeatScheduler {

    companion object{
        private const val TAG = "HearbeatSchedulerImpl"
    }

    private var curMinHeart = IHeartbeatScheduler1.MIN_HEART_TIME
    private var curMaxHeart = IHeartbeatScheduler1.MAX_HEART_TIME

    private val maxFailedCount = IHeartbeatScheduler1.MAX_FAILED_COUNT
    private var maxSuccessCount =IHeartbeatScheduler1.MAX_SUCCESS_COUNT

    private val heartbeatMap = HashMap<String, Heartbeat>()
    private val successHeartList = CopyOnWriteArrayList<Int>()

    @Volatile
    private var networkTag = ""
    @Volatile
    protected var started = false
    @Volatile
    protected var heartbeatSuccessTime: Long = 0
    @Volatile
    protected var currentHeartType: Int = 0
    private val requestCode = 700

    override fun start(context: Context) {
        started = true
        networkTag = NetworkUtil.getCurrentNetStateCode(context).toString()
        alarm(context)
        Log.i(TAG, "start heartbeat,networkTag:$networkTag")
    }

    private fun getHeartbeat(): Heartbeat {
        var heartbeat: Heartbeat? = heartbeatMap[networkTag]
        if (heartbeat == null) {
            heartbeat = Heartbeat()
            heartbeatMap[networkTag] = heartbeat
        }
        return heartbeat
    }

    protected fun createPendingIntent(context: Context, requestCode: Int, heartType: Int): PendingIntent {
        val intent = Intent(context, MedImReceiver::class.java)
        intent.setPackage(context.packageName)
        intent.action = IHeartbeatScheduler1.HEART_BEAT_ACTION
        intent.putExtra(IHeartbeatScheduler.HEART_TYPE_TAG, heartType)
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun alarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val heartbeat = getHeartbeat()
        val stabled = heartbeat.stabled.get()
        var heart: Int
        if (stabled) {
            heart = heartbeat.curHeart - 10
            if (heart < IHeartbeatScheduler1.MIN_HEART_TIME) {
                heart = IHeartbeatScheduler1.MIN_HEART_TIME
            }
            heart *= 1000
        } else {
            heart = heartbeat.curHeart * 1000
        }
        val heartType = if (stabled) IHeartbeatScheduler1.STABLE_HEART else IHeartbeatScheduler1.PROBE_HEART
        val pendingIntent = createPendingIntent(context, requestCode, heartType)
        val sdk = Build.VERSION.SDK_INT
        if (sdk >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + heart, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + heart, pendingIntent)
        }
        Log.i(TAG, "start heartbeat,curHeart [${heartbeat.curHeart}],heart [$heart],requestCode:$requestCode,stabled:$stabled")
    }

    override fun stop(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clear(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun adjustHeart(context: Context, success: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun receiveHeartbeatFailed(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun receiveHeartbeatSuccess(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}