package blog.pds.com.socket.app

import android.app.Application
import blog.pds.com.socket.control.config.ApiManager

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-08 16:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */
object SocketManager{
    var application: Application? = null
    const val ip = "192.168.2.2"
    const val port = 6666

    public fun init(application: Application){
        this.application = application
        ApiManager.ipAndPort(
            ip,
            port
        )
    }
}