package blog.pds.com.socket.control.core

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import blog.pds.com.socket.ISocketAIDLSendData
import blog.pds.com.socket.core.client.CService
import blog.pds.com.socket.core.client.SAction
import blog.pds.com.socket.IMemoryAidlInterface
import blog.pds.com.socket.control.Constants
import blog.pds.com.socket.control.SocketControl
import blog.pds.com.socket.control.config.ApiManager
import blog.pds.com.socket.control.dispatch.SocketReceiveDataBinder
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean


/**
 * @author: pengdaosong
 * CreateTime:  2019-08-09 14:49
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SocketServiceManager {

    companion object{
        private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE+"ssm:"
    }

    private var socketSendDataBinder : blog.pds.com.socket.ISocketAIDLSendData? = null
    private var intent : Intent? = null
    private val bServiceBinded = AtomicBoolean(false)

    fun socketInit(context: Context) {
        val intent = createSocketServiceIntent(context, SAction.OP_TYPE_INIT)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    /**
     * 创建socket服务对象
     */
    private fun createSocketServiceIntent(context: Context, opType: Int): Intent? {
        intent = Intent(context, CService::class.java)
        intent?.putExtra(SAction.KEY_OP_TYPE, opType)
        intent?.putExtra(Constants.KEY_IP, ApiManager.ip)
        intent?.putExtra(Constants.KEY_PORT, ApiManager.port)
        return intent
    }

    /**
     * bind service
     */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG,"service binder success")
            bServiceBinded.set(true)
            socketSendDataBinder = blog.pds.com.socket.ISocketAIDLSendData.Stub.asInterface(service)
            try {
                socketSendDataBinder?.reregisterCallback(SocketReceiveDataBinder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG,"service binder failed")
            bServiceBinded.set(false)
            socketSendDataBinder = null
            SocketControl.context?.let { startAndBinderService(it,SAction.OP_TYPE_RECONNECT) }
        }

    }

    /**
     * 匿名内存传输数据
     */
    private val memoryConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val content = ByteArray(10)
            val iMemoryAidlInterface = IMemoryAidlInterface.Stub.asInterface(service)
            try {
                val parcelFileDescriptor = iMemoryAidlInterface.parcelFileDescriptor
                val descriptor = parcelFileDescriptor.fileDescriptor
                val fileInputStream = FileInputStream(descriptor)
                fileInputStream.read(content)
            } catch (e: Exception) {
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            SocketControl.context?.let { startAndBinderService(it,SAction.OP_TYPE_RECONNECT) }
        }

    }

    /**
     * 开启和绑定服务
     */
    fun startAndBinderService(context: Context,opType: Int){
        val intent = createSocketServiceIntent(context,opType)
        startSocketService(context,createSocketServiceIntent(context,opType))
        bindSocketService(context,createSocketServiceIntent(context,opType))
    }

    /**
     * 开启和绑定服务
     */
    fun stopAndUnbinderService(context: Context){
        stopSocketService(context)
        unBindSocketService(context)
    }

    /**
     * 开启服务
     */
    fun startSocketService(context: Context,opType: Int){
        startSocketService(context,createSocketServiceIntent(context,opType))
    }
    fun startSocketService(context: Context,intent: Intent?){
        context.startService(intent)
    }

    /**
     * 停止服务
     */
    fun stopSocketService(context: Context){
        context.stopService(intent)
    }

    /**
     * 绑定服务
     */
    fun bindSocketService(context: Context){

        bindSocketService(context,intent)
    }

    fun bindSocketService(context: Context,intent: Intent?){
        if (!isServiceRunning()){
            context.bindService(intent,serviceConnection,Service.BIND_AUTO_CREATE)
        }
    }

    /**
     * 解绑服务
     */
    fun unBindSocketService(context: Context){
        context.unbindService(serviceConnection)
    }

    fun checkSocketService(context: Context){
        if(!isServiceRunning()){
            startAndBinderService(context,SAction.OP_TYPE_CONNECT)
        }
    }

    fun isServiceRunning(): Boolean {
        return (socketSendDataBinder != null
                && socketSendDataBinder?.asBinder() != null
                && socketSendDataBinder?.asBinder()?.isBinderAlive!!)
    }
}