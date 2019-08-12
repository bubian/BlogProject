package blog.pds.com.socket.control.core

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import blog.pds.com.socket.ISocketAIDLSendData
import blog.pds.com.socket.core.client.CService
import blog.pds.com.socket.core.client.SAction

/**
 * @author: pengdaosong
 * CreateTime:  2019-08-09 14:49
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SocketManager {

    private var socketSendDataBinder : ISocketAIDLSendData? = null
    fun socketInit(context: Context) {
        val intent = createSocketServiceIntent(context, SAction.OP_TYPE_INIT)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    /**
     * 创建socket服务对象
     */
    private fun createSocketServiceIntent(context: Context, opType: Int): Intent {
        val intent = Intent(context, CService::class.java)
        intent.putExtra(SAction.KEY_OP_TYPE, opType)
        return intent
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            socketSendDataBinder = ISocketAIDLSendData.Stub.asInterface(service)
            try {
                socketSendDataBinder?.reregisterCallback(SocketReceiveDataBinder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }
}