package blog.pds.com.socket .control

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import blog.pds.com.socket.control.config.ApiManager
import blog.pds.com.socket.control.config.ConstantManager
import blog.pds.com.socket.control.core.SocketServiceManager
import blog.pds.com.socket.control.dispatch.SocketCallbackManager
import blog.pds.com.socket.core.client.SAction
import blog.pds.com.socket.core.client.SState

/**
 * @author: pengdaosong
 * CreateTime:  2019-08-29 17:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class SocketControl{

    companion object{
        const val TAG = "${ConstantManager.PRE_TAG}sc"
        var context: Application? = null
        val socketServiceManager = SocketServiceManager()

        fun init(context: Application){
            SocketControl.context = context
        }

        /**
         * 开启socket服务
         */
        fun startSocketService(context: Context,opType: Int){
            socketServiceManager.startSocketService(context,opType)
        }

        /**
         * 停止socket服务
         */
        fun stopSocketService(context: Context){
            socketServiceManager.stopSocketService(context)
        }

        /**
        * 开启并且绑定socket服务
        */
        fun startAndBinderService(context: Context,opType: Int){
            socketServiceManager.startAndBinderService(context,opType)
        }

        /**
         * 停止并且解绑socket服务
         */
        fun stopAndUnbinderService(context: Context){
            socketServiceManager.stopAndUnbinderService(context)
        }

        /**
         * 连接socket
         */
        fun connectSocket(context: Context){
            connectSocket(context,ApiManager.ip,ApiManager.port)
        }

        /**
         * 连接socket
         */
        fun connectSocket(context: Context,ip: String?,port: Int?){
            ApiManager.ip = ip
            ApiManager.port = port
            if (!checkIpAndPort()) {
                Log.i(TAG,"start connect")
                startAndBinderService(context,SAction.OP_TYPE_CONNECT)
            }else{
                Log.i(TAG,"ip or port error:ip = $ip port = $port")
                SocketCallbackManager.socketCallbackList.forEach {
                    it.socketConnectState(SState.STATE_CONNECT_FAILED)
                }
            }
        }

        private fun checkIpAndPort() : Boolean{
            return null == ApiManager.ip || null == ApiManager.port
                    || ApiManager.ip.isNullOrBlank() || ApiManager.port!! <= 0
        }

    }
}