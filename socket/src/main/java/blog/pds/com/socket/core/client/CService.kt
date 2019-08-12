package blog.pds.com.socket.core.client

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.text.TextUtils
import android.util.Log
import blog.pds.com.socket.core.common.*
import blog.pds.com.socket.core.dispatch.ReceiveSocketDataDispatch
import blog.pds.com.socket.core.dispatch.SocketSendDataBinder
import blog.pds.com.socket.core.manager.SocketManager
import blog.pds.com.socket.core.thread.AsyncTaskExecutor
import blog.pds.com.socket.core.thread.RunnablePool
import java.lang.ref.SoftReference


/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:15 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class CService : Service(){

    companion object {
        private const val GRAY_SERVICE_ID = 0xff
        private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE +"cs:"
    }

    private lateinit var iClientSoft : SoftReference<ISocket>
    private var startSocketId : Long = 0
    private val receiveSocketDataDispatch = ReceiveSocketDataDispatch()

    private fun imClient(): CSocket {
        if (null == iClientSoft.get()) {
            createImClient()
        }
        if (null == iClientSoft.get()) {
            throw NullPointerException("im client is null")
        }
        return iClientSoft.get() as CSocket
    }

    override fun onBind(intent: Intent?): IBinder? {
        return SocketSendDataBinder.asBinder()
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate pid=${Process.myPid()}")
        createImClient()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val cSocket = imClient()
        if (null == intent){
            Log.i(TAG, "onStartCommand intent == null , pid=${Process.myPid()} , mIClient = $cSocket")
            AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_RECONNECT))
            return START_STICKY
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val innerIntent = Intent(this, GrayInnerService::class.java)
            startService(innerIntent)
            startForeground(GRAY_SERVICE_ID, Notification())
        }

        // 执行动作类型
        val opType = intent.getIntExtra(SAction.KEY_OP_TYPE, -1)
        Log.i(TAG, "onStartCommand intent:opType = $opType")

        when(opType){
            SAction.OP_TYPE_INIT -> {}
            // 连接socket
            SAction.OP_TYPE_CONNECT -> {
                if (!isAbortSocketConnectRequest(cSocket)) connectSocket(intent)
            }
            // 断开socket
            SAction.OP_TYPE_DISCONNECT -> {
                startSocketId = -1
                AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_DISCONNECT))
            }
            // 重新连接socket
            SAction.OP_TYPE_RECONNECT -> {
                if (!cSocket.isConnected()) {
                    AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_RECONNECT))
                }
            }
            // 发送消息
            SAction.OP_TYPE_SEND -> {
                sendMessage(intent)
            }
            else -> {}
        }
        return START_STICKY
    }

    /**
     *  socket已经连接、或者正在连接
     */
    private fun isAbortSocketConnectRequest(cSocket:CSocket): Boolean {
        return cSocket.getConnectState() == SState.STATE_CONNECTING
                || cSocket.getConnectState() == SState.STATE_CONNECTED
    }

    /**
     * 创建socket连接实例
     */
    private fun createImClient() {
        CSocket.registerCallback(receiveSocketDataDispatch.socketCallback)
        iClientSoft = SoftReference(CSocket)
    }

    /**
     * 连接socket
     */
    private fun connectSocket(intent: Intent) {
        val ip = intent.getStringExtra(Constants.KEY_IP)
        val port = intent.getIntExtra(Constants.KEY_PORT, -1)
        Log.d(TAG,"connectSocket:ip = $ip port = $port")
        if (TextUtils.isEmpty(ip) || port < 0) {
            Log.e(TAG,"socket parameter exception")
            return
        }
        AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_CONNECT, ip, port))
    }

    /**
     * 异步执行
     */
    private val mRunnableExecutorImpl = object : RunnablePool.IRunnbleExecutor {
        override fun execute(what: Int,  params: Array<out Any>) {
            // 检测实例是否被系统回收
            val iClient = imClient()

            for (c in params!!){
                Log.d(TAG,"runner.params = $c")
            }
            when (what) {
                SAction.OP_TYPE_CONNECT -> iClient.connect(params[0].toString(), params[1] as Int)
                SAction.OP_TYPE_SEND -> iClient.send(params[0] as ByteArray, params[1] as ISendCallBack)
                SAction.OP_TYPE_DISCONNECT -> iClient.disConnect(false)
                SAction.OP_TYPE_RECONNECT -> iClient.reConnect()
                else -> {
                }
            }
        }
    }

    /**
     * 发送消息
     */
    private fun sendMessage(intent: Intent) {
        val bytes = intent.getByteArrayExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA)
        AsyncTaskExecutor.execute(
            RunnablePool.obtain(
                mRunnableExecutorImpl,
                SAction.OP_TYPE_SEND,
                bytes,
                object : ISendCallBack {
                    override fun onSuccess() {

                    }

                    override fun onFailed(e: Exception) {
                        SocketManager.reconnect()
                    }
                })
        )
    }


    class GrayInnerService : Service() {

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            startForeground(GRAY_SERVICE_ID, Notification())
            //            stopForeground(true);
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }

    }


    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        Log.i(TAG, "unbindService conn=$conn ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy pid=${Process.myPid()}")

    }
}