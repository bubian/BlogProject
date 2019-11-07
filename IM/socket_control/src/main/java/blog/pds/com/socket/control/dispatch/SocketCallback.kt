package blog.pds.com.socket.control.dispatch

import blog.pds.com.socket.core.client.SState

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-04 16:12
 * Email：pengdaosong@medlinker.com
 * Description:
 */
interface SocketCallback{
    fun onReceive(type: Int, data: ByteArray?)
    fun socketConnectState(state: Int)
}