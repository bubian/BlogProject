package blog.pds.com.socket.core.manager

import blog.pds.com.socket.core.heartbeat.HeartBeatManager
import java.nio.ByteBuffer

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 2:41 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SocketDispatch {
    companion object {
        fun reconnect() {
            TimeIntervalManager.instance(object : TimeIntervalManager.SimpleTimeIntervalCallback() {
                override fun accept() {
//                    SocketServiceHelper.connect(BaseApplication.app(),11,"192.168.1.2",6666)
                }
            })
        }

        /**
         *
         */
        fun connect(startSocketId: Long, ip: String, port: Int) {
//            SocketServiceHelper.connect(BaseApplication.app(),startSocketId, ip, port)
        }

        /**
         *
         */
        fun disConnect() {
//            SocketServiceHelper.disConnect(BaseApplication.app())
            HeartBeatManager.stopHeatBeat()
        }

        /**
         * 发送ping给服务器
         */
        fun sendPing() {
            HeartBeatManager.interval()
            val byteBuffer = ByteBuffer.allocate(3)
            val ping = byteArrayOf(3, 0, 0)
            byteBuffer.put(ping)
            sendMessage(byteBuffer.array())
        }

        /**
         * 发送pong给服务器
         */
        fun sendPong() {
            val byteBuffer = ByteBuffer.allocate(3)
            val pong = byteArrayOf(4, 0, 0)
            byteBuffer.put(pong)
            sendMessage(byteBuffer.array())
        }


        /**
         * @param msg
         */
        private fun sendMessage(msg: ByteArray) {
//            SocketServiceHelper.sendMessage(BaseApplication.app(),msg)
        }

        /**
         * 检测消息进程是否还存活，不存活重新连接
         * net.medlinker.medlinker:imremote
         */
        fun checkImRomoteProcessAlive() {
//            val activityManager = BaseApplication.app().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            val infos = activityManager.getRunningAppProcesses()
//            var isAlive = false
//            for (info in infos) {
//                isAlive = info.processName == "blog.pds.com.socket:cService"
//                if (isAlive) {
//                    break
//                }
//            }
//            Log.i("ImManager", "$isAlive--进程存活状态")
//            if (!isAlive) {
//                //如果发消息的时候，进程没有在运行，就重新初始化IM
//                reconnect()
//            }
        }
    }
}