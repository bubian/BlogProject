package blog.pds.com.socket.control.dispatch

import android.util.Log
import blog.pds.com.socket.ISocketAIDLReceiveData
import blog.pds.com.socket.control.Constants


/**
 * @author: pengdaosong
 * CreateTime:  2019-08-09 15:56
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object SocketReceiveDataBinder : ISocketAIDLReceiveData.Stub() {
    private const val TAG = Constants.SOCKET_TAG_CLIENT_PRE + "srdb:"

    override fun socketConnectState(state: Int) {
        SocketCallbackManager.socketCallbackList.forEach {
            it.socketConnectState(state)
        }
    }

    override fun receiveSocketData(type: Int,data: ByteArray?) {
        Log.d(TAG,"binder receive data")
        SocketCallbackManager.socketCallbackList.forEach {
            it.onReceive(type,data)
        }
    }
}