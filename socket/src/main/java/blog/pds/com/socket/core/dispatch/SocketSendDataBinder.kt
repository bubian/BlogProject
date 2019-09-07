package blog.pds.com.socket.core.dispatch

import android.util.Log
import blog.pds.com.socket.ISocketAIDLReceiveData
import blog.pds.com.socket.ISocketAIDLSendData
import blog.pds.com.socket.core.common.Constants


/**
 * @author: pengdaosong
 * CreateTime:  2019-07-31 11:11
 * Email：pengdaosong@medlinker.com
 * Description:
 */
object SocketSendDataBinder : ISocketAIDLSendData.Stub(){

    private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE + "ssdb:"

    private var socketAIDLReceiveData: ISocketAIDLReceiveData? = null

    override fun sendSocketData(data: ByteArray?): Boolean {
        return true
    }

    /**
     * 注册数据回调
     */
    override fun reregisterCallback(socketAIDLReciveData: ISocketAIDLReceiveData?): Boolean {
        this.socketAIDLReceiveData = socketAIDLReciveData
        return true
    }

    /**
     * 销毁数据回调
     */
    override fun unReregisterCallback(): Boolean {
        socketAIDLReceiveData = null
        return true
    }

    /**
     * 接收到socket传输过来的数据
     */

    fun onReceive(type: Int, data: ByteArray) {
        if (!checkNull(socketAIDLReceiveData)){
            Log.d(TAG,"binder dispatch data")
            socketAIDLReceiveData?.receiveSocketData(type,data)
        }
    }

    private fun checkNull(any: Any?): Boolean {
        return null == any
    }
}