package blog.pds.com.socket.core.dispatch

import blog.pds.com.socket.ISocketAIDLSendData

/**
 * @author: pengdaosong
 * CreateTime:  2019-07-31 11:11
 * Email：pengdaosong@medlinker.com
 * Description:
 */
object SocketSendDataBinder : ISocketAIDLSendData.Stub(){
    override fun sendSocketData(data: ByteArray?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}