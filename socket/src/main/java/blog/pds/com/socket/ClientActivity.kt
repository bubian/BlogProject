package blog.pds.com.socket

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import blog.pds.com.socket.core.client.SocketServiceHelper
import blog.pds.com.socket.core.common.Constants

class ClientActivity : AppCompatActivity() {

    private lateinit var socketConnectState : TextView
    private lateinit var socketAcceptData : TextView
    private lateinit var socketIp : EditText
    private lateinit var openConnect : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_activity)

        socketConnectState = findViewById(R.id.socket_connect_state)
        socketAcceptData = findViewById(R.id.client_data)
        socketIp = findViewById(R.id.ip)
        openConnect = findViewById(R.id.connect)

        openConnect.setOnClickListener {
            startSocketService()
//            socketConnectState.text = "正在创建连接!"
//            it.isEnabled = false
        }
    }

    private fun startSocketService(){
        var ip : String?= socketIp.text.toString()

        if (null == ip || ip.isEmpty()){
            ip = "10.0.4.16"
        }
        SocketServiceHelper.connect(this,10000,ip,Constants.SOCKET_PORT)
    }


}
