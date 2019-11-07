package blog.pds.com.socket.core.heartbeat

import android.content.Context

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:05
 * Email：pengdaosong@medlinker.com
 * Description:
 */
interface IHeartbeatScheduler{
    companion object{
        const val TIME_OUT = 20

        const val MIN_HEART_TIME = 60
        const val MAX_HEART_TIME = 300

        const val MAX_FAILED_COUNT = 5
        const val MAX_SUCCESS_COUNT = 10


        const val HEART_TYPE_TAG = "heart_type"
        const val HEART_BEAT_ACTION = "heart_beat"
        const val UNKNOWN_HEART = 0
        const val SHORT_HEART = 1
        const val PROBE_HEART = 2
        const val STABLE_HEART = 3
        const val REDUNDANCY_HEART = 4
    }

    fun start(context: Context)
    fun stop(context: Context)
    fun clear(context: Context)
    fun adjustHeart(context: Context,success: Boolean)
    fun receiveHeartbeatFailed(context: Context)
    fun receiveHeartbeatSuccess(context: Context)
}