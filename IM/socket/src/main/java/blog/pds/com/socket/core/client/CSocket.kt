package blog.pds.com.socket.core.client

import android.util.Log
import blog.pds.com.socket.core.common.*
import blog.pds.com.socket.core.policy.CRetryPolicy
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:22 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object CSocket : ISocket {

    private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE + "CSocket:"
    private var socket : Socket? = null
    private var dataOutputStream: DataOutputStream? = null
    private var dataInputStream: DataInputStream? = null
    private var ip: String? = null
    private var port: Int = 0
    private var lock: Lock = ReentrantLock()
    private var connectState : Int = SState.STATE_NONE
    private var cRetryPolicy : CRetryPolicy? = null

    private lateinit var sCallback: SCallback

    init {
        cRetryPolicy = CRetryPolicy()
    }

    fun registerCallback(sCallback: SCallback){
        this.sCallback = sCallback
    }

    override fun connect(ip: String, port: Int) {
        lock.lock()
        if (isConnected()){
            disConnect(false)
        }
        connectState = SState.STATE_CONNECTING
        this.ip = ip
        this.port = port
        Log.i(TAG,"connecting  ip=$ip , port = $port")
        this.sCallback.onConnect()
        try {
            while (true){
                try {
                    socket = Socket()
                    if (null == socket){
                        throw (Exception("connect failed,unknown error"))
                    }
                    val address = InetSocketAddress(ip,port)
                    socket!!.keepAlive = false
                    //inputStream read 超时时间
                    socket!!.soTimeout = 2 * 3 * 60 * 1000
                    socket!!.tcpNoDelay = true
                    socket!!.connect(address)
                    if (socket!!.isConnected){
                        dataInputStream = DataInputStream(socket!!.getInputStream())
                        dataOutputStream = DataOutputStream(socket!!.getOutputStream())
                        connectState = SState.STATE_CONNECTED
                        this.sCallback.connected()
                        break
                    }else{
                        throw (Exception("connect failed,unknown error"))
                    }
                }catch (e:Exception){
                    cRetryPolicy?.retry(e)
                    Thread.sleep(5*1000)
                    Log.i(TAG,"connect IOException =${e.message} , and retry count = ${cRetryPolicy?.getCurrentRetryCount()}")
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            Log.i(TAG,"connect IOException =  ${e.message}")
            connectState = SState.STATE_CONNECT_FAILED
            sCallback.onConnectFailed(e)
        }finally {
            lock.unlock()
        }
        if (connectState == SState.STATE_CONNECTED){
            read()
        }else{
            disConnect(false)
            sCallback.onConnectFailed(Exception("connect failed,unknown error"))
        }
    }

    private fun read(){
        while (isConnected()){
            try {
                val type = dataInputStream?.readByte()!!.toInt()
                Log.i(TAG,"receiveData:type = $type")

                val length = dataInputStream?.readChar()!!.toInt()
                Log.i(TAG,"receiveData:length = $length")
                val data = ByteArray(length)

                Log.i(TAG,"receiveData:data-length = ${data.size}")

                dataInputStream?.readFully(data)
                Log.i(TAG,"receiveData:data = ${String(data,Charsets.ISO_8859_1)}")

                sCallback.onReceive(type,data)
            }catch (e:SocketTimeoutException){
                Log.e(TAG,"receiveData SocketTimeoutException = ${e.message}")
                break
            }catch (e: IOException){
                Log.e(TAG,"receiveData IOException = ${e.message}")
                break
            }
        }
        sCallback.onDisconnect()
    }

    override fun write(bytes: ByteArray, callback: ISendCallBack) {
        synchronized(CSocket.javaClass){
            if (isConnected()){
                try {
                    dataOutputStream?.write(bytes)
                    dataOutputStream?.flush()
                    callback.onSuccess()
                    Log.i(TAG,"end success msg : ${Arrays.toString(bytes)}")
                }catch (e:Exception){
                    e.printStackTrace()
                    callback.onFailed(e)
                    disConnect(true)
                }
            }else{
                disConnect(true)
                Log.e(TAG,"socket is not connected !")
            }
        }
    }

    /**
     * 重新连接socket
     */
    fun reConnect(){
        if (!ip.isNullOrBlank() && port > 0){
            connect(ip!!, port)
        }
    }

    /**
     * 关闭socket
     */
    override fun disConnect(reconnect:Boolean) {
        Log.i(TAG,"disConnect")
        if(null != socket){
            try {
                closeInputStream()
                closeOutputStream()
                socket?.shutdownInput()
                socket?.shutdownOutput()
                socket?.close()
                socket = null
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        connectState = SState.STATE_DISCONNECT
        if (reconnect){
            reConnect()
        }else{
            sCallback.onDisconnect()
        }
    }

    /**
     * 关闭socket输入流
     */
    private fun closeInputStream(){
        try {
            dataInputStream!!.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 关闭socket输出流
     */
    private fun closeOutputStream(){
        try {
            dataOutputStream!!.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    /**
     * 是否存在已经连接成功的socket
     */
    override fun isConnected() : Boolean{
        if (null == socket){
            return false
        }
        return socket!!.isConnected && connectState == SState.STATE_CONNECTED
    }

    override fun getConnectState(): Int {
       return connectState
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun connect(ip: String, port: Int,time: Int): Int
}