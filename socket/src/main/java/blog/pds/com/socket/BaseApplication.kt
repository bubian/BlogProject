package blog.pds.com.socket

import android.app.Application

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class BaseApplication :Application(){

    companion object{
        private lateinit var application: BaseApplication

        fun app(): BaseApplication {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

}