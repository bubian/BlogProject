package blog.pds.com.socket.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 4:42 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SService : Service(){

    companion object {
//        private const val TAG = Constants.SOCKET_TAG_SERVICE_PRE+"SS:"
    }

    private val sSocket = SSocket()

    override fun onCreate() {
        super.onCreate()
//        Log.i(TAG,"onCreate")
        Thread(Runnable {
            sSocket.startAccept()
        }).start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        sSocket.stop()
    }
}