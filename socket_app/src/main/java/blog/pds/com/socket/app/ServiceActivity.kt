package blog.pds.com.socket.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:09 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class ServiceActivity : AppCompatActivity(){

    private lateinit var socketConnectState : TextView
    private lateinit var socketAcceptData : TextView
    private lateinit var socketInfo : TextView
    private lateinit var openAccept : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_activity)

        socketConnectState = findViewById(R.id.socket_connect_state)
        socketAcceptData = findViewById(R.id.service_data)
        socketInfo = findViewById(R.id.socket_info)
        openAccept = findViewById(R.id.start_accept)
        openAccept.setOnClickListener {
            if (openAccept.isSelected) {
                openAccept.text = "开启socket监听"
                stopAccept()
            } else {
                openAccept.text = "断开监听"
                startAccept()
            }
            openAccept.isSelected = !openAccept.isSelected
        }
    }

    private fun startAccept(){
        val i = Intent(this, SService::class.java)
        this.startService(i)
    }

    private fun stopAccept(){
        val i = Intent(this, SService::class.java)
        this.stopService(i)
    }
}