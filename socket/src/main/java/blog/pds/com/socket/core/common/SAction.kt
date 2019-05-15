package blog.pds.com.socket.core.common

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 12:39 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SAction{
    companion object {
        /**
         * 操作service类型key
         */
       const val KEY_OP_TYPE = "KEY_OP_TYPE"
        /**
         * socket连接
         */
        const val OP_TYPE_CONNECT = 1


        /**
         * socket 断开连接
         */
        const val OP_TYPE_DISCONNECT = 2


        /**
         * socket 重新连接
         */
        const val OP_TYPE_RECONNECT = 3


        /**
         * 发送消息
         */
        const val OP_TYPE_SEND = 4

        /**
         * service 初始化
         */
        const val OP_TYPE_INIT = 10
    }
}