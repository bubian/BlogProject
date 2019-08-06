package blog.pds.com.socket.core.client

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 2:26 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface SCallback{

    /**
     * 开始socket连接
     */
    fun onConnect()

    /**
     * socket连接成功
     */
    fun connected()

    /**
     * socket断开
     */
    fun onDisconnect()

    /**
     * socket连接失败
     */
    fun onConnectFailed(ex: Exception)

    /**
     * 接收socket消息
     */
    fun onReceive(type: Int, data: ByteArray)

    /**
     * 接收socket消息
     */
    fun onReceive(data: ByteArray)

    /**
     * 发送socket消息
     */
    fun send(bytes: ByteArray, callback: ISendCallBack)

}