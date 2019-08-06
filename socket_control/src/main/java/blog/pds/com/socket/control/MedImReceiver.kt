package blog.pds.com.socket.core.client

import android.content.Context
import blog.pds.com.socket.core.common.Constants
import blog.pds.com.socket.core.common.PacketType
import blog.pds.com.socket.core.dispatch.ImBaseReceiver
import blog.pds.com.socket.core.heartbeat.HeartBeatManager
import blog.pds.com.socket.core.manager.SocketManager
import blog.pds.com.socket.core.utils.SocketServiceHelper

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-25 17:08
 * Email：pengdaosong@medlinker.com
 * Description:
 */
open class MedImReceiver : ImBaseReceiver(){

    companion object{
        private const val TAG = "MedImReceiver"
    }


    override fun onReceiveSocketLocalMsg(context: Context, msgWhat: Int) {
        when (msgWhat) {
            Constants.LOCAL_MSG_CONNECT -> {
                // socket连接完成，发送版本号
                val imVersion = "  V1"
                SocketServiceHelper.sendMessage(context,imVersion.toByteArray())
            }
            Constants.LOCAL_MSG_DISCONNECTED, Constants.LOCAL_MSG_CONNECT_FAILED ->
                //内部连接失败，或者通知断开连接，这里统一重连
                //延迟5s重连 , 这里不判断网络状态，就不用监听网络变化。
                SocketManager.reconnect()
        }
    }

    override fun onReceiveSocketRemoteMsg(context: Context, msgType: Int, byteArrayExtra: ByteArray) {
        when (msgType) {
            PacketType.CONNECT -> {}
            PacketType.CONNECTED -> {
                HeartBeatManager.beginHeartBeat()//连接成功，开始心跳
            }
            PacketType.CLOSE -> {}
            PacketType.RECONNECT -> {
                SocketManager.disConnect()
                SocketManager.reconnect()
            }
            PacketType.PING -> {
                SocketManager.sendPong()
                HeartBeatManager.cancleLastHeartBeat()
            }
            PacketType.PONG -> HeartBeatManager.receivedPong()
            PacketType.MESSAGE -> {
                HeartBeatManager.cancleLastHeartBeat()
            }
        }

    }
}