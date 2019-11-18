package blog.pds.com.socket.core.dispatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import blog.pds.com.socket.core.common.Constants
import blog.pds.com.socket.core.heartbeat.IHeartbeatScheduler
import blog.pds.com.socket.core.manager.SocketDispatch

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-25 16:44
 * Email：pengdaosong@medlinker.com
 * Description:
 */
open class ImBaseReceiver: BroadcastReceiver(){
    companion object{
        const val TAG = "ImBaseReceiver"
    }
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action!!.equals(Constants.IM_RECEIVER_ACTION, ignoreCase = true)) {
            val msgType = intent.getIntExtra(Constants.KEY_MESSAGE_TYPE, -1)
            Log.i(TAG, "onReceive with permission action=${intent.action},  pid=${Process.myPid()} , timeStamp=${System.currentTimeMillis()} ,type = $msgType")
            if (msgType == Constants.MESSAGE_TYPE_REMOTE) {
                val type = intent.getIntExtra(Constants.KEY_REMOTE_SOCKET_MSG_TYPE, -999)
                val data = intent.getByteArrayExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA)
                onReceiveSocketRemoteMsg(context, type, data)
            } else if (msgType == Constants.MESSAGE_TYPE_LOCAL) {
                val msgWhat = intent.getIntExtra(Constants.KEY_LOCAL_SOCKET_MSG, -1)
                onReceiveSocketLocalMsg(context, msgWhat)
            }
        } else if (intent != null && intent.action!!.equals(IHeartbeatScheduler.HEART_BEAT_ACTION, ignoreCase = true)) {
            //接受到心跳包发送心跳
            SocketDispatch.sendPing()
        }
    }

    /**
     * @param context
     * @param msgWhat 本地socket连接相关信息
     */
    protected open fun onReceiveSocketLocalMsg(context: Context, msgWhat: Int) {}

    /**
     * 接收server返回消息
     *
     * @param context
     * @param msgType        消息类型
     * @param byteArrayExtra 消息数据
     */
    protected open fun onReceiveSocketRemoteMsg(context: Context, msgType: Int, byteArrayExtra: ByteArray) {}

}