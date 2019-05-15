package blog.pds.com.socket.core.common

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
    }
}