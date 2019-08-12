package blog.pds.com.socket.core.dispatch

import blog.pds.com.socket.ISocketAIDLReceiveData
import blog.pds.com.socket.ISocketAIDLSendData


/**
 * @author: pengdaosong
 * CreateTime:  2019-07-31 11:11
 * Email：pengdaosong@medlinker.com
 * Description:
 */
object SocketSendDataBinder : ISocketAIDLSendData.Stub(){


    override fun sendSocketData(data: ByteArray?): Boolean {
        return true
    }

    override fun reregisterCallback(socketAIDLReciveData: ISocketAIDLReceiveData?): Boolean {
        return true
    }

    override fun unReregisterCallback(): Boolean {
        return true
    }

}