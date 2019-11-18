package blog.pds.com.socket.core.manager

import android.content.Context

/**
 * @author: pengdaosong
 * CreateTime:  2019-11-07 17:36
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object GlobalVar{

    private var application:Context? = null
    public fun init(application: Context){
        this.application = application
    }

    public fun application(): Context?{
        return application
    }
}