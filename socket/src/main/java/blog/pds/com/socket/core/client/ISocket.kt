package blog.pds.com.socket.core.client

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:23 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface ISocket{
    /**
     * 创建socket连接
     * @param ip 服务器ip
     * @param port 非服务器端口
     */
    fun connect(ip: String,port: Int)
    /**
     * 断开连接
     */
    fun disConnect(reconnect:Boolean)
    /**
     * socket是否连接
     */
    fun isConnected(): Boolean
    /**
     * @return 返回socket连接状态
     */
    fun getConnectState(): Int

    /**
     * 发送socket数据
     */
    fun write(bytes: ByteArray, callback: ISendCallBack)
}
