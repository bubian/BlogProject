package blog.pds.com.socket.app.common.util

import blog.pds.com.socket.app.common.SState

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-05 14:17
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SocketConnectStateUtils{

    companion object{
        fun getStateStr(state: Int): String {

            return when(state){
                SState.STATE_CONNECT_FAILED -> {
                    "socket 连接失败"
                }else -> {
                    "未知状态"
                }
            }
        }
    }
}