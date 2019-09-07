package blog.pds.com.socket.app.client

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import blog.pds.com.data.protobuf.SocketPackage
import blog.pds.com.socket.app.R
import blog.pds.com.socket.app.util.SocketConnectStateUtils
import blog.pds.com.socket.control.SocketControl
import blog.pds.com.socket.control.dispatch.SimpleSocketCallback
import blog.pds.com.socket.control.dispatch.SocketCallbackManager
import java.net.Socket

class ClientActivity : AppCompatActivity(){

    companion object{
        const val TAG = "ClientActivity"
    }

    private lateinit var socketConnectState : TextView
    private lateinit var socketAcceptData : TextView
    private lateinit var socketIp : EditText
    private lateinit var openConnect : Button

//    companion object {
//        init {
//            System.loadLibrary("socket_client-lib")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_activity)

        socketConnectState = findViewById(R.id.socket_connect_state)
        socketAcceptData = findViewById(R.id.client_data)
        socketIp = findViewById(R.id.ip)
        openConnect = findViewById(R.id.connect)

        openConnect.setOnClickListener {
            startSocketService()
            socketConnectState.text = "正在创建连接!"
        }

        SocketCallbackManager.registerSocketCallback(object: SimpleSocketCallback(){
            override fun socketConnectState(state: Int) {
                Log.d(TAG,SocketConnectStateUtils.getStateStr(state))
            }

            override fun onReceive(type: Int, data: ByteArray?) {
                val  pk = SocketPackage.Package.parseFrom(data)
                Log.e(TAG,pk.toString())
            }
        })
    }

    private fun startSocketService(){
        SocketControl.connectSocket(this)
    }

}
