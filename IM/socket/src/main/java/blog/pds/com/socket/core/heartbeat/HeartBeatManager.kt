package blog.pds.com.socket.core.heartbeat

import android.util.Log.*
import blog.pds.com.socket.core.manager.GlobalVar
import blog.pds.com.socket.core.manager.SocketDispatch
import blog.pds.com.socket.core.manager.TimeIntervalManager

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object HeartBeatManager {
    private const val TAG = "HeartBeatManager"
    private val heartbeatScheduler = HearbeatSchedulerImpl()
    private val context = null
    private const val period = 1000L

    private val timeIntervalManager =
        TimeIntervalManager.instance(object : TimeIntervalManager.SimpleTimeIntervalCallback() {
            override fun accept() {
                val context = GlobalVar.application() ?: return
                heartbeatScheduler.receiveHeartbeatFailed(context)
                SocketDispatch.reconnect()
            }
        })

    fun beginHeartBeat() {
        val context = GlobalVar.application() ?: return
        i(TAG, "begin heart beat")
        heartbeatScheduler.start(context)
    }

    fun interval() {
        i(TAG, "发送ping")
        timeIntervalManager.interval(period)
    }

    /**
     * 收到服务器返回的pong,
     */
    fun receivedPong() {
        val context = GlobalVar.application() ?: return
        i(TAG, "received pong")
        //成功获得心跳，调整稳定心跳值
        heartbeatScheduler.receiveHeartbeatSuccess(context)
        i(TAG, "stop interval")
        timeIntervalManager.stop()
    }


    /**
     * 如果有消息再传输，则取消上一次的心跳，由于上行没有通过socket所以这里暂时没有添加上行
     */
    fun cancelLastHeartBeat() {
        val context = GlobalVar.application() ?: return
        i(TAG, "cancel last heart beat")
        //如果有消息在socket发送，则取消上一个心跳延迟
        heartbeatScheduler.stop(context)
        //重新开始心跳逻辑
        heartbeatScheduler.start(context)
    }

    /**
     * 断开连接停止心跳
     */
    fun stopHeatBeat() {
        val context = GlobalVar.application() ?: return
        i(TAG, "stop heat beat")
        heartbeatScheduler.start(context)
        heartbeatScheduler.clear(context)
    }
}