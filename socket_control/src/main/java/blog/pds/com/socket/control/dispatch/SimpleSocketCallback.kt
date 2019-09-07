package blog.pds.com.socket.control.dispatch

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-04 16:14
 * Email：pengdaosong@medlinker.com
 * Description:
 */
open class SimpleSocketCallback : SocketCallback{
    override fun socketConnectState(state: Int) {
    }

    override fun onReceive(type: Int, data: ByteArray?) {

    }
}