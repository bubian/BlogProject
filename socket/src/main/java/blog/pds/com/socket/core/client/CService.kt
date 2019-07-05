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
import blog.pds.com.socket.SocketAIDLService
import blog.pds.com.socket.core.common.*
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

    override fun onBind(intent: Intent?): IBinder? {
        return binder
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
            return Service.START_STICKY
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val innerIntent = Intent(this, GrayInnerService::class.java)
            startService(innerIntent)
            startForeground(GRAY_SERVICE_ID, Notification())
        }

        val opType = intent.getIntExtra(SAction.KEY_OP_TYPE, -1)

        Log.i(
            TAG,
            "onStartCommand intent: $intent , flags=$flags, startId=$startId , pid=${Process.myPid()} , " +
                    "opType = $opType, isConnected = ${cSocket.isConnected()}, connectStatus = ${cSocket.getConnectState()}"
        )

        when(opType){
            SAction.OP_TYPE_INIT -> {

            }
            SAction.OP_TYPE_CONNECT -> {
                val newStartSocketId = intent.getLongExtra(Constants.KEY_START_SOCKET_ID, -1L)
                if (newStartSocketId <= 0 || newStartSocketId == startSocketId
                    && (cSocket.getConnectState() === SState.STATE_CONNECTING || cSocket.getConnectState() === SState.STATE_CONNECTED)) {
                    //userid 不合法 或者 socket已经连接、或者正在连接
                    Log.e(
                        TAG,
                        "onStartCommand intent op connect , intercept newstartUserId = $newStartSocketId, startUserid = $startSocketId, socket connectState = ${cSocket.getConnectState()}"
                    )
                }
                startSocketId = newStartSocketId
                connectSocket(intent)
            }
            SAction.OP_TYPE_DISCONNECT -> {
                startSocketId = -1
                AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_DISCONNECT))
            }
            SAction.OP_TYPE_RECONNECT -> {
                if (!cSocket.isConnected()) {
                    AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_RECONNECT))
                }
            }

            SAction.OP_TYPE_SEND -> {
                sendMessage(intent)
            }
            else -> {

            }

        }

        return Service.START_STICKY

    }

    private fun connectSocket(intent: Intent) {
        val ip = intent.getStringExtra(Constants.KEY_IP)
        val port = intent.getIntExtra(Constants.KEY_PORT, -1)
        if (TextUtils.isEmpty(ip) || port < 0) {
            return
        }
        Log.d(TAG,"connectSocket:ip = $ip port = $port")
        AsyncTaskExecutor.execute(RunnablePool.obtain(mRunnableExecutorImpl, SAction.OP_TYPE_CONNECT, ip, port))
    }

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

    /**
     * socket连接匿名内部类
     */
    private val socketCallback = object : SCallback {
        override fun onConnect() {

        }

        override fun onDisconnect() {

        }

        override fun onConnectFailed(ex: Exception) {

        }

        override fun onReceive(type: Int, data: ByteArray) {

        }

        override fun send(bytes: ByteArray, callback: ISendCallBack) {

        }

    }

    private val mRunnableExecutorImpl = object : RunnablePool.IRunnbleExecutor {
        override fun execute(what: Int,  params: Array<out Any>) {
            val iClient = imClient()
            for (c in params!!){
                Log.d(TAG,"runner.params = $c")
            }
            when (what) {
                SAction.OP_TYPE_CONNECT -> iClient.connect(params[0].toString(), params[1] as Int)
                SAction.OP_TYPE_SEND -> iClient.send(params[0] as ByteArray, params[1] as ISendCallBack)
                SAction.OP_TYPE_DISCONNECT ->
                    //手动断开，不需要重连。
                    iClient.disConnect(false)
                SAction.OP_TYPE_RECONNECT -> iClient.reConnect()
                else -> {
                }
            }
        }
    }

    private fun createImClient() {
        CSocket.registerCallback(socketCallback)
        iClientSoft = SoftReference(CSocket)
    }

    private fun imClient(): CSocket {
        if (null == iClientSoft.get()) {
            Log.i(TAG, "iClientSoft is null again create")
            createImClient()
        }
        if (null == iClientSoft.get()) {
            Log.i(TAG, "fetch im client error")
            throw NullPointerException("im client is null")
        }
        return iClientSoft.get() as CSocket
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

    private val binder = object : SocketAIDLService.Stub(){
        override fun isSocketConnected(): Boolean {
            val iClient = imClient()
            return iClient.isConnected()
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