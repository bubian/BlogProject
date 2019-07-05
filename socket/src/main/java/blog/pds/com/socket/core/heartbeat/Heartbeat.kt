package blog.pds.com.socket.core.heartbeat

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:37
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class Heartbeat{

    /**
     * 心跳连续成功次数
     */
    internal var heartbeatStabledSuccessCount = AtomicInteger(0)
    /**
     * 心跳连续失败次数
     */
    internal var heartbeatFailedCount = AtomicInteger(0)

    internal var successHeart: Int = 0

    internal var failedHeart: Int = 0

    internal var curHeart = 180

    internal var stabled = AtomicBoolean(false)
}