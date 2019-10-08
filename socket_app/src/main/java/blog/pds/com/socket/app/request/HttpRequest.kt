package blog.pds.com.socket.app.request

import android.os.Build
import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-17 16:09
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class HttpRequest{

    private var client : OkHttpClient = OkHttpClient()
    private val atomicBoolean = AtomicBoolean(false)

    companion object{
        private const val TAG = "HttpRequest"
        private const val URL_GITHUB = "https://ww1.sinaimg.cn/large/0073sXn7ly1fze96s6tdag308w0ftjvw"
        private const val URL_HTTPS_PICTURE = "https://app.medlinker.com/rest/v1/users/login"
    }
    fun requestPictureList(){
        if (atomicBoolean.get()){
            return
        }
        atomicBoolean.set(true)
        Thread {
            val request = Request.Builder().url(URL_GITHUB).build()
            val response =  client.newCall(request).execute()
            Log.d(TAG,response.body?.string())
            atomicBoolean.set(false)
        }.start()
    }

    fun requestHttpsPicture(){

        if (atomicBoolean.get()){
            return
        }
        atomicBoolean.set(true)
        Thread {

            val requestBody = FormBody
                .Builder()
                .add("sys_p","a")
                .add("sys_m", Build.MODEL)
                .add("sys_v",Build.VERSION.RELEASE)
                .add("sys_vc", Build.VERSION.SDK_INT.toString())
                .add("cli_c","medlinker")
                .add("cli_v","7.5.1")
                .add("sys_pkg","net.medlinker.medlinker")
                .add("username","15708468804")
                .add("password","123456")
                .build()
            val request = Request
                .Builder()
                .url(URL_HTTPS_PICTURE)
                .post(requestBody)
                .build()
            val response =  client.newCall(request).execute()
            Log.d(TAG,response.body?.string())
            atomicBoolean.set(false)
        }.start()
    }
}