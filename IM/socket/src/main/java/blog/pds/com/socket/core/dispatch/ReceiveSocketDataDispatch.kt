package blog.pds.com.socket.core.dispatch

import android.util.Log
import blog.pds.com.socket.core.client.ISendCallBack
import blog.pds.com.socket.core.client.SCallback
import blog.pds.com.socket.core.common.Constants
import blog.pds.com.socket.core.common.PacketType
import blog.pds.com.socket.core.heartbeat.HeartBeatManager
import blog.pds.com.socket.core.manager.SocketDispatch

/**
 * @author: pengdaosong
 * CreateTime:  2019-07-30 17:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class ReceiveSocketDataDispatch{

    companion object{
        private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE +"rsdd:"
    }

    /**
     * socket消息回执
     */
    val socketCallback = object : SCallback {
        override fun onReceive(type: Int, data: ByteArray) {
            when(type){
                // 开始链接socket
                PacketType.CONNECT -> {

                }
                // 收到服务器确认链接成功消息
                PacketType.CONNECTED ->{
                    HeartBeatManager.beginHeartBeat()//连接成功，开始心跳
                }
                // socket关闭
                PacketType.CLOSE ->{

                }

                PacketType.RECONNECT ->{

                }
                // 发送心跳包
                PacketType.PING ->{
                    SocketDispatch.sendPong()
                }
                // 收到服务器返回的心跳包
                PacketType.PONG ->{
                    HeartBeatManager.cancelLastHeartBeat()
                }

                PacketType.MESSAGE ->{
                    HeartBeatManager.cancelLastHeartBeat()
                }

                else ->{
                    Log.e(TAG,"unknown socket data package!")
                }
            }
            SocketSendDataBinder.onReceive(type,data)
        }

        override fun connected() {

        }

        override fun onConnect() {

        }

        override fun onDisconnect() {

        }

        override fun onConnectFailed(ex: Exception) {

        }

        override fun send(bytes: ByteArray, callback: ISendCallBack) {

        }
    }
}