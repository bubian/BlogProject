package blog.pds.com.socket.app

import androidx.multidex.MultiDexApplication

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class ModuleApplication : MultiDexApplication(){

    companion object{
        private lateinit var application: ModuleApplication

        const val ip = "192.168.2.2"
        const val port = 6666

        fun app(): ModuleApplication {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        SocketManager.init(this)
    }

}