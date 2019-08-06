package blog.pds.com.socket.core.common

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-25 17:12
 * Email：pengdaosong@medlinker.com
 * Description:
 */
interface PacketType {

    @IntDef(
        CONNECT,
        CONNECTED,
        RECONNECT,
        PING,
        PONG,
        MESSAGE,
        CLOSE
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class ImHeadType

    companion object {

        const val CONNECT = 0x00 // 建立连接，由server发起
        const val CONNECTED = 0x01 // 连接已建立，由server发起
        const val RECONNECT = 0x02 // 重建连接，由server发起
        const val PING = 0x03 // 心跳包，client与server都可发起
        const val PONG = 0x04 // 心跳响应包，client与server都可发起
        const val MESSAGE = 0x05 // 业务消息，由server发起
        const val CLOSE = 0x07 // 被踢掉，不需要重连
    }

}