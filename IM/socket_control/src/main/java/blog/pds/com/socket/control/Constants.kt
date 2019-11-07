package blog.pds.com.socket.control

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:49 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

/**
 * 单例模式
 */
class Constants{

    companion object {
        /**
         * socket日志打印前缀
         */
        const val SOCKET_TAG_CLIENT_PRE = "socket_client:"
        const val SOCKET_TAG_SERVICE_PRE = "socket_service:"
        /**
         * 启动socket服务的用户id
         */
        const val KEY_START_SOCKET_ID = "KEY_START_SOCKET_ID"

        const val KEY_IP = "key_str_ip"
        const val KEY_PORT = "key_str_port"
        const val KEY_REMOTE_SOCKET_MSG_DATA = "key_remote_socket_msg_data"

        const val SOCKET_PORT = 6666

        const val IM_RECEIVER_ACTION = "android.intent.action.im.receiver_action"
        const val KEY_REMOTE_SOCKET_MSG_TYPE = "key_remote_socket_msg_type"
        const val KEY_MESSAGE_TYPE = "key_message_type"
        const val KEY_LOCAL_SOCKET_MSG = "key_local_socket_msg"

        /**
         * 远程socket消息
         */
        const val MESSAGE_TYPE_REMOTE = 20
        /**
         * 本地socket消息。主要是连接相关消息。
         */
        const val MESSAGE_TYPE_LOCAL = 21
        const val LOCAL_MSG_CONNECT = 30
        const val LOCAL_MSG_DISCONNECTED = 31
        const val LOCAL_MSG_CONNECT_FAILED = 32

    }
}