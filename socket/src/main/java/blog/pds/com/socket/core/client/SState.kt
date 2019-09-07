package blog.pds.com.socket.core.client

import android.support.annotation.IntDef

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 2:39 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface SState{

    @IntDef(
        STATE_NONE,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_CONNECT_FAILED,
        STATE_DISCONNECT
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ImHeadType

    companion object {

        const val STATE_NONE = 0x10 // 无状态
        const val STATE_CONNECTING = 0x11 // 正在连接
        const val STATE_CONNECTED = 0x12 // 已连接
        const val STATE_CONNECT_FAILED = 0x13 // 连接失败
        const val STATE_DISCONNECT = 0x14 // 已断开连接
    }
}