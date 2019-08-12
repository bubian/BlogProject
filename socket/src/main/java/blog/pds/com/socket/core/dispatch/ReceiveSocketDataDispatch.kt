package blog.pds.com.socket.core.dispatch

import blog.pds.com.socket.core.client.ISendCallBack
import blog.pds.com.socket.core.client.SCallback
import blog.pds.com.socket.core.common.Constants

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
        override fun onReceive(data: ByteArray) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun connected() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

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
}