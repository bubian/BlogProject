package blog.pds.com.socket.control.config

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-04 17:01
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object ApiManager{
    var ip: String? = ""
    var port: Int? = 0

    fun ipAndPort(ip: String?,port: Int?){
        this.ip = ip
        this.port = port
    }
}