package com.pds.web.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtil {

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        return haveNetwork(context);
    }

    public static boolean haveNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo[] networkInfoList = cm.getAllNetworkInfo();
            if (networkInfoList != null) {
                for (int i = 0; i < networkInfoList.length; i++) {
                    // 判断网络是否已连接
                    if (networkInfoList[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } else {
            final Network[] networks = cm.getAllNetworks();
            if (networks != null && networks.length > 0) {
                NetworkInfo info;
                for (Network network : networks) {
                    info = cm.getNetworkInfo(network);
                    if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
