package blog.pds.com.socket.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 17:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class NetworkUtil {

    /**
     * 枚举网络状态
     * NET_NO：没有网络 , NET_2G:2g网络 , NET_3G：3g网络 ,NET_4G：4g网络 ,NET_WIFI：wifi , NET_UNKNOWN：未知网络
     */
    enum class NetState{
        NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

    companion object{
        /**
         * 判断网络是否可用
         *
         * @param context
         * @return
         */
        fun isConnected(context: Context): Boolean {
            return haveNetwork(context)
        }

        /**
         * 判断WIFI是否连接
         *
         * @param context
         * @return
         */
        fun isWifiConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
            val wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state
            return wifiState == NetworkInfo.State.CONNECTED
        }

        /**
         * 没有使用枚举应该效果更好。
         * 建议在receiver中监听网络切换，维护一个网络状态在内存中。
         *
         * @param context
         * @return
         */
        fun getCurrentNetStateCode(context: Context): NetState {
            var stateCode = NetState.NET_NO
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            if (ni != null && ni.isConnectedOrConnecting) {
                when (ni.type) {
                    //wifi
                    ConnectivityManager.TYPE_WIFI -> stateCode = NetState.NET_WIFI
                    //mobile 网络
                    ConnectivityManager.TYPE_MOBILE -> when (ni.subtype) {
                        TelephonyManager.NETWORK_TYPE_GPRS //联通2g
                            , TelephonyManager.NETWORK_TYPE_CDMA //电信2g
                            , TelephonyManager.NETWORK_TYPE_EDGE //移动2g
                            , TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> stateCode =
                            NetState.NET_2G
                        TelephonyManager.NETWORK_TYPE_EVDO_A //电信3g
                            , TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> stateCode =
                            NetState.NET_3G
                        TelephonyManager.NETWORK_TYPE_LTE//4G
                        -> stateCode = NetState.NET_4G
                        //未知,一般不会出现
                        else -> stateCode = NetState.NET_UNKNOWN
                    }
                    else -> stateCode = NetState.NET_UNKNOWN
                }
            }
            return stateCode
        }

        fun haveNetwork(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val networkInfoList = cm.allNetworkInfo
                if (networkInfoList != null) {
                    for (i in networkInfoList.indices) {
                        // 判断网络是否已连接
                        if (networkInfoList[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            } else {
                val networks = cm.allNetworks
                if (networks != null && networks.size > 0) {
                    var info: NetworkInfo?
                    for (network in networks) {
                        info = cm.getNetworkInfo(network)
                        if (info != null && info.state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }

}