package blog.pds.com.socket.app.common.base

import androidx.multidex.MultiDexApplication
import blog.pds.com.socket.control.config.ApiManager

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class BaseApplication : MultiDexApplication(){

    companion object{
        private lateinit var application: BaseApplication

        const val ip = "192.168.2.2"
        const val port = 6666

        fun app(): BaseApplication {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        ApiManager.ipAndPort(
            ip,
            port
        )
    }

}