package blog.pds.com.socket.core.utils

import android.content.Context
import android.content.Intent
import blog.pds.com.socket.core.client.CService
import blog.pds.com.socket.core.client.SAction
import blog.pds.com.socket.core.common.Constants

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 2:53 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class SocketServiceHelper{

    companion object {
        fun connect(context: Context,startSocketId: Long, ip: String, port: Int) {
            val intent = createImServiceIntent(
                context,
                SAction.OP_TYPE_CONNECT
            )
            intent.putExtra(Constants.KEY_IP, ip)
            intent.putExtra(Constants.KEY_PORT, port)
            intent.putExtra(Constants.KEY_START_SOCKET_ID, startSocketId)
            context.startService(intent)
        }

        fun disConnect(context: Context) {
            val intent = createImServiceIntent(
                context,
                SAction.OP_TYPE_DISCONNECT
            )
            context.startService(intent)
        }

        fun reConnect(context: Context) {
            val intent = createImServiceIntent(
                context,
                SAction.OP_TYPE_RECONNECT
            )
            context.startService(intent)
        }

        fun sendMessage(context: Context,msg: ByteArray) {
            val intent = createImServiceIntent(
                context,
                SAction.OP_TYPE_SEND
            )
            intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA, msg)
            try {
                context.startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun createImServiceIntent(context: Context,opType: Int): Intent {
            val i = Intent(context, CService::class.java)
            i.putExtra(SAction.KEY_OP_TYPE, opType)
            return i
        }
    }
}