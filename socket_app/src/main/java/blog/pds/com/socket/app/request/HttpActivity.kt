package blog.pds.com.socket.app.request

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import blog.pds.com.socket.app.R

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-17 16:23
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class HttpActivity : AppCompatActivity(){

    private val httpRequest = HttpRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)
        // http
        findViewById<Button>(R.id.btn_http).setOnClickListener {
            httpRequest.requestPictureList()
        }
        // https
        findViewById<Button>(R.id.btn_https).setOnClickListener{
            httpRequest.requestHttpsPicture()
        }
    }
}