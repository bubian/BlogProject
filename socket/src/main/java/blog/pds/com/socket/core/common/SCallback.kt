package blog.pds.com.socket.core.common

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 2:26 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface SCallback{

    fun onConnect()

    fun onDisconnect()

    fun onConnectFailed(ex: Exception)

    fun onReceive(type: Int, data: ByteArray)

    fun send(bytes: ByteArray, callback: ISendCallBack)

}