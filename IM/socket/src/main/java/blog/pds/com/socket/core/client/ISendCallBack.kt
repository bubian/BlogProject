package blog.pds.com.socket.core.client

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 4:25 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface ISendCallBack{
    fun onSuccess()
    fun onFailed(e: Exception)
}