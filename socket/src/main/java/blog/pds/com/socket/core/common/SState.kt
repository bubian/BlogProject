package blog.pds.com.socket.core.common

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 2:39 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

enum class SState{
    /**
     * 无状态
     */
    STATE_NONE,
    /**
     * 正在连接
     */
    STATE_CONNECTING,
    /**
     * 已连接
     */
    STATE_CONNECTED,
    /**
     * 连接失败
     */
    STATE_CONNECT_FAILED,
    /**
     * 已断开连接
     */
    STATE_DISCONNECT
}