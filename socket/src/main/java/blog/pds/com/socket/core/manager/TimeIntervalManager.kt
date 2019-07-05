package blog.pds.com.socket.core.manager

import android.os.Handler
import android.os.Looper
import blog.pds.com.socket.core.ITimeIntervalCallBack

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 18:05
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class TimeIntervalManager private constructor(timeIntervalCallback: ITimeIntervalCallBack) {
    companion object{
        private const val TAG = "TimeIntervalManager"

        fun instance(iTimeInterval: ITimeIntervalCallBack): TimeIntervalManager {
            return TimeIntervalManager(iTimeInterval)
        }
    }
    private val handler : Handler? = Handler(Looper.getMainLooper())
    private var timeIntervalCallback : ITimeIntervalCallBack? = timeIntervalCallback
    private var stop = true

    fun interval(period :Long){
        if (null == timeIntervalCallback || null == handler)return
        if (stop){
            stop = false
            timeIntervalCallback?.start()
        }
        handler.postDelayed({
            timeIntervalCallback?.accept()
            if (!stop){
                interval(period)
            }

        },period)
    }

    fun delay(delay :Long){
        handler?.postDelayed({
            timeIntervalCallback?.accept()
        },delay)

    }

    fun stop(){
        handler?.removeCallbacksAndMessages(null)
        stop = true
        timeIntervalCallback?.stop()
    }

    open class SimpleTimeIntervalCallback : ITimeIntervalCallBack {
        override fun accept() {}
        override fun stop() {}
        override fun start() {}
    }
}