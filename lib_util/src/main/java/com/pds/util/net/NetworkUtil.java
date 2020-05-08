package com.pds.util.net;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @description 网络状态相关工具类
 * @time 2015/10/31 12:48
 */
public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    /**
     * 网络类型 - 无连接
     */
    public static final int NETWORK_TYPE_NO_CONNECTION = -1231545315;

    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_3G = "eg";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        return haveNetwork(context);
    }

    /**
     * 判断WIFI是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifiState == NetworkInfo.State.CONNECTED;
    }

    /**
     * 判断当前网络是否已经断开
     *
     * @param context 上下文
     * @return 当前网络是否已经断开
     */
    public static boolean isDisconnectedByState(Context context) {
        return getCurrentNetworkState(context) ==
                NetworkInfo.State.DISCONNECTED;
    }

    /**
     * 判断当前网络是否正在断开
     *
     * @param context 上下文
     * @return 当前网络是否正在断开
     */
    public static boolean isDisconnectingByState(Context context) {
        return getCurrentNetworkState(context) ==
                NetworkInfo.State.DISCONNECTING;
    }

    /**
     * 判断当前网络是否已经暂停
     *
     * @param context 上下文
     * @return 当前网络是否已经暂停
     */
    public static boolean isSuspendedByState(Context context) {
        return getCurrentNetworkState(context) == NetworkInfo.State.SUSPENDED;
    }

    /**
     * 判断当前网络是否处于未知状态中
     *
     * @param context 上下文
     * @return 当前网络是否处于未知状态中
     */
    public static boolean isUnknownByState(Context context) {
        return getCurrentNetworkState(context) == NetworkInfo.State.UNKNOWN;
    }

    /**
     * 判断当前网络的类型是否是蓝牙
     *
     * @param context 上下文
     * @return 当前网络的类型是否是蓝牙。false：当前没有网络连接或者网络类型不是蓝牙
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static boolean isBluetoothByType(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return false;
        }
        else {
            return getCurrentNetworkType(context) ==
                    ConnectivityManager.TYPE_BLUETOOTH;
        }
    }

    /**
     * 判断当前网络的类型是否是虚拟网络
     *
     * @param context 上下文
     * @return 当前网络的类型是否是虚拟网络。false：当前没有网络连接或者网络类型不是虚拟网络
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean isDummyByType(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return false;
        }
        else {
            return getCurrentNetworkType(context) ==
                    ConnectivityManager.TYPE_DUMMY;
        }
    }

    /**
     * 判断当前网络的类型是否是ETHERNET
     *
     * @param context 上下文
     * @return 当前网络的类型是否是ETHERNET。false：当前没有网络连接或者网络类型不是ETHERNET
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static boolean isEthernetByType(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return false;
        }
        else {
            return getCurrentNetworkType(context) ==
                    ConnectivityManager.TYPE_ETHERNET;
        }
    }

    /**
     * 判断当前网络的类型是否是移动网络
     *
     * @param context 上下文
     * @return 当前网络的类型是否是移动网络。false：当前没有网络连接或者网络类型不是移动网络
     */
    public static boolean isMobileByType(Context context) {
        return getCurrentNetworkType(context) ==
                ConnectivityManager.TYPE_MOBILE;
    }


    /**
     * 判断当前网络的类型是否是MobileDun
     *
     * @param context 上下文
     * @return 当前网络的类型是否是MobileDun。false：当前没有网络连接或者网络类型不是MobileDun
     */
    public static boolean isMobileDunByType(Context context) {
        return getCurrentNetworkType(context) ==
                ConnectivityManager.TYPE_MOBILE_DUN;
    }


    /**
     * 判断当前网络的类型是否是MobileHipri
     *
     * @param context 上下文
     * @return 当前网络的类型是否是MobileHipri。false：当前没有网络连接或者网络类型不是MobileHipri
     */
    public static boolean isMobileHipriByType(Context context) {
        return getCurrentNetworkType(context) ==
                ConnectivityManager.TYPE_MOBILE_HIPRI;
    }


    /**
     * 判断当前网络的类型是否是MobileMms
     *
     * @param context 上下文
     * @return 当前网络的类型是否是MobileMms。false：当前没有网络连接或者网络类型不是MobileMms
     */
    public static boolean isMobileMmsByType(Context context) {
        return getCurrentNetworkType(context) ==
                ConnectivityManager.TYPE_MOBILE_MMS;
    }


    /**
     * 判断当前网络的类型是否是MobileSupl
     *
     * @param context 上下文
     * @return 当前网络的类型是否是MobileSupl。false：当前没有网络连接或者网络类型不是MobileSupl
     */
    public static boolean isMobileSuplByType(Context context) {
        return getCurrentNetworkType(context) ==
                ConnectivityManager.TYPE_MOBILE_SUPL;
    }


    /**
     * 是否打开移动数据流量
     * @param context
     * @return
     */
    public static boolean isMobileEnable(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 枚举网络状态
     * NET_NO：没有网络 , NET_2G:2g网络 , NET_3G：3g网络 ,NET_4G：4g网络 ,NET_WIFI：wifi , NET_UNKNOWN：未知网络
     */
    public enum NetState {
        NET_NO, NET,NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

    /**
     * 没有使用枚举应该效果更好。
     * 建议在receiver中监听网络切换，维护一个网络状态在内存中。
     *
     * @param context
     * @return
     */
    public static NetState getCurrentNetStateCode(Context context) {
        NetState stateCode = NetState.NET_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                //wifi
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                //mobile 网络
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE://4G
                            stateCode = NetState.NET_4G;
                            break;
                        //未知,一般不会出现
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }
        }
        return stateCode;
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

    /**
     * 读取设备的有线网络的地址
     *
     * @return AA-BB-CC-DD-EE-FF 格式 如果不存在，返回""
     */
    public static String getLANMac() {
        String mac = getMacLevel9("eth[0-9]+");
        if (mac.equals("")) {
            mac = getMacNetcfg("eth[0-9]+");
        }

        // MAC地址总是使用大写的。
        if (!TextUtils.isEmpty(mac)) {
            mac.toUpperCase(Locale.CHINA);
        }

        if (mac.length() != 17) {
            Log.e(TAG, "getLANMac mac:" + mac);
        } else {
            Log.i(TAG, "getLANMac mac:" + mac);
        }

        return mac;
    }

    public static String getWifiMac() {
        String mac = getMacLevel9("wlan[0-9]+");
        if (mac.equals("")) {
            mac = getMacNetcfg("wlan[0-9]+");
        }
        if (!TextUtils.isEmpty(mac)) {
            mac.toUpperCase(Locale.CHINA);
        }
        if (mac.length() != 17) {
            Log.e(TAG, "getWLANMac mac:" + mac);
        } else {
            Log.i(TAG, "getWLANMac mac:" + mac);
        }
        return mac;
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要API Level 9,
     * <uses-permission android:name="android.permission.INTERNET"/>
     */
    private static String getMacLevel9(String name_pattern_rgx) {
        try {
            Method getHardwareAddress = NetworkInterface.class.getMethod("getHardwareAddress");
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface n = nis.nextElement();
                getHardwareAddress.invoke(n);
                byte[] hw_addr = (byte[]) getHardwareAddress.invoke(n);
                if (hw_addr != null) {
                    String name = n.getName().toLowerCase(Locale.CHINA);
                    String mac = MacString(hw_addr);
                    Log.d("NetTools.getMacLevel9", name + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static String MacString(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (byte v : mac) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(String.format("%02X", v));
        }
        return sb.toString();
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要netcfg工具, <uses-permission
     * android:name="android.permission.INTERNET"/>
     */
    private static String getMacNetcfg(String name_pattern_rgx) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("netcfg");
            BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            Pattern result_pattern = Pattern
                    .compile("^([a-z0-9]+)\\s+(UP|DOWN)\\s+([0-9./]+)\\s+.+\\s+([0-9a-f:]+)$", Pattern.CASE_INSENSITIVE);
            while (is.ready()) {
                String info = is.readLine();
                Matcher m = result_pattern.matcher(info);
                if (m.matches()) {
                    String name = m.group(1).toLowerCase(Locale.CHINA);
                    String status = m.group(2);
                    String addr = m.group(3);
                    String mac = m.group(4).toUpperCase(Locale.CHINA).replace(':', '-');
                    Log.d("NetTools.getMacNetcfg", "match success:" + name + " " + status + " " + addr + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断当前的网络连接类型是否是WiFi
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static NetState getNetState(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetState.NET_WIFI;
            } else {
                return NetState.NET;
            }

        }
        return NetState.NET_NO;
    }

    /**
     * Get network type
     *
     * @param context context
     * @return 网络状态
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null
                ? null
                : connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    /**
     * 获取当前网络的类型
     *
     * @param context 上下文
     * @return 当前网络的类型。具体类型可参照ConnectivityManager中的TYPE_BLUETOOTH、TYPE_MOBILE、TYPE_WIFI等字段。当前没有网络连接时返回NetworkUtils.NETWORK_TYPE_NO_CONNECTION
     */
    public static int getCurrentNetworkType(Context context) {
        NetworkInfo networkInfo
                = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null
                ? networkInfo.getType()
                : NETWORK_TYPE_NO_CONNECTION;
    }


    /**
     * Get network type name
     *
     * @param context context
     * @return NetworkTypeName
     */
    public static String getNetworkTypeName(Context context) {
        ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        String type = NETWORK_TYPE_DISCONNECT;
        if (manager == null ||
                (networkInfo = manager.getActiveNetworkInfo()) == null) {
            return type;
        }
        ;

        if (networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(typeName)) {
                type = NETWORK_TYPE_WIFI;
            }
            else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context)
                        ? NETWORK_TYPE_3G
                        : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            }
            else {
                type = NETWORK_TYPE_UNKNOWN;
            }
        }
        return type;
    }

    /**
     * Whether is fast mobile network
     *
     * @param context context
     * @return FastMobileNetwork
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager
                = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取当前网络的状态
     *
     * @param context 上下文
     * @return 当前网络的状态。具体类型可参照NetworkInfo.State.CONNECTED、NetworkInfo.State.CONNECTED.DISCONNECTED等字段。当前没有网络连接时返回null
     */
    public static NetworkInfo.State getCurrentNetworkState(Context context) {
        NetworkInfo networkInfo
                = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null ? networkInfo.getState() : null;
    }


    /**
     * 获取当前网络的具体类型
     *
     * @param context 上下文
     * @return 当前网络的具体类型。具体类型可参照TelephonyManager中的NETWORK_TYPE_1xRTT、NETWORK_TYPE_CDMA等字段。当前没有网络连接时返回NetworkUtils.NETWORK_TYPE_NO_CONNECTION
     */
    public static int getCurrentNetworkSubtype(Context context) {
        NetworkInfo networkInfo
                = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null
                ? networkInfo.getSubtype()
                : NETWORK_TYPE_NO_CONNECTION;
    }

    /**
     * 判断当前网络的类型是否是Wifi
     *
     * @param context 上下文
     * @return 当前网络的类型是否是Wifi。false：当前没有网络连接或者网络类型不是wifi
     */
    public static boolean isWifiByType(Context context) {
        return getCurrentNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }


    /**
     * 判断当前网络的类型是否是Wimax
     *
     * @param context 上下文
     * @return 当前网络的类型是否是Wimax。false：当前没有网络连接或者网络类型不是Wimax
     */
    public static boolean isWimaxByType(Context context) {
        return getCurrentNetworkType(context) == ConnectivityManager.TYPE_WIMAX;
    }


    /**
     * 判断当前网络的具体类型是否是1XRTT
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是1XRTT。false：当前没有网络连接或者具体类型不是1XRTT
     */
    public static boolean is1XRTTBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_1xRTT;
    }


    /**
     * 判断当前网络的具体类型是否是CDMA（Either IS95A or IS95B）
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是CDMA。false：当前没有网络连接或者具体类型不是CDMA
     */
    public static boolean isCDMABySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_CDMA;
    }


    /**
     * 判断当前网络的具体类型是否是EDGE
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是EDGE。false：当前没有网络连接或者具体类型不是EDGE
     */
    public static boolean isEDGEBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_EDGE;
    }


    /**
     * 判断当前网络的具体类型是否是EHRPD
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是EHRPD。false：当前没有网络连接或者具体类型不是EHRPD
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isEHRPDBySubtype(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return false;
        }
        else {
            return getCurrentNetworkSubtype(context) ==
                    TelephonyManager.NETWORK_TYPE_EHRPD;
        }
    }


    /**
     * 判断当前网络的具体类型是否是EVDO_0
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是EVDO_0。false：当前没有网络连接或者具体类型不是EVDO_0
     */
    public static boolean isEVDO_0BySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_EVDO_0;
    }


    /**
     * 判断当前网络的具体类型是否是EVDO_A
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是EVDO_A。false：当前没有网络连接或者具体类型不是EVDO_A
     */
    public static boolean isEVDO_ABySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_EVDO_A;
    }


    /**
     * 判断当前网络的具体类型是否是EDGE
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是EVDO_B。false：当前没有网络连接或者具体类型不是EVDO_B
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isEVDO_BBySubtype(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        else {
            return getCurrentNetworkSubtype(context) ==
                    TelephonyManager.NETWORK_TYPE_EVDO_B;
        }
    }


    /**
     * 判断当前网络的具体类型是否是GPRS
     * EVDO_Bam context 上下文
     *
     * @return false：当前网络的具体类型是否是GPRS。false：当前没有网络连接或者具体类型不是GPRS
     */
    public static boolean isGPRSBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_GPRS;
    }


    /**
     * 判断当前网络的具体类型是否是HSDPA
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是HSDPA。false：当前没有网络连接或者具体类型不是HSDPA
     */
    public static boolean isHSDPABySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_HSDPA;
    }


    /**
     * 判断当前网络的具体类型是否是HSPA
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是HSPA。false：当前没有网络连接或者具体类型不是HSPA
     */
    public static boolean isHSPABySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_HSPA;
    }


    /**
     * 判断当前网络的具体类型是否是HSPAP
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是HSPAP。false：当前没有网络连接或者具体类型不是HSPAP
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static boolean isHSPAPBySubtype(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return false;
        }
        else {
            return getCurrentNetworkSubtype(context) ==
                    TelephonyManager.NETWORK_TYPE_HSPAP;
        }
    }


    /**
     * 判断当前网络的具体类型是否是HSUPA
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是HSUPA。false：当前没有网络连接或者具体类型不是HSUPA
     */
    public static boolean isHSUPABySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_HSUPA;
    }


    /**
     * 判断当前网络的具体类型是否是IDEN
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是IDEN。false：当前没有网络连接或者具体类型不是IDEN
     */
    public static boolean isIDENBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_IDEN;
    }


    /**
     * 判断当前网络的具体类型是否是LTE
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是LTE。false：当前没有网络连接或者具体类型不是LTE
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isLTEBySubtype(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return false;
        }
        else {
            return getCurrentNetworkSubtype(context) ==
                    TelephonyManager.NETWORK_TYPE_LTE;
        }
    }


    /**
     * 判断当前网络的具体类型是否是UMTS
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是UMTS。false：当前没有网络连接或者具体类型不是UMTS
     */
    public static boolean isUMTSBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_UMTS;
    }


    /**
     * 判断当前网络的具体类型是否是UNKNOWN
     *
     * @param context 上下文
     * @return false：当前网络的具体类型是否是UNKNOWN。false：当前没有网络连接或者具体类型不是UNKNOWN
     */
    public static boolean isUNKNOWNBySubtype(Context context) {
        return getCurrentNetworkSubtype(context) ==
                TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }


    /**
     * 判断当前网络是否是中国移动2G网络
     *
     * @param context 上下文
     * @return false：不是中国移动2G网络或者当前没有网络连接
     */
    public static boolean isChinaMobile2G(Context context) {
        return isEDGEBySubtype(context);
    }


    /**
     * 判断当前网络是否是中国联通2G网络
     *
     * @param context 上下文
     * @return false：不是中国联通2G网络或者当前没有网络连接
     */
    public static boolean isChinaUnicom2G(Context context) {
        return isGPRSBySubtype(context);
    }


    /**
     * 判断当前网络是否是中国联通3G网络
     *
     * @param context 上下文
     * @return false：不是中国联通3G网络或者当前没有网络连接
     */
    public static boolean isChinaUnicom3G(Context context) {
        return isHSDPABySubtype(context) || isUMTSBySubtype(context);
    }


    /**
     * 判断当前网络是否是中国电信2G网络
     *
     * @param context 上下文
     * @return false：不是中国电信2G网络或者当前没有网络连接
     */
    public static boolean isChinaTelecom2G(Context context) {
        return isCDMABySubtype(context);
    }


    /**
     * 判断当前网络是否是中国电信3G网络
     *
     * @param context 上下文
     * @return false：不是中国电信3G网络或者当前没有网络连接
     */
    public static boolean isChinaTelecom3G(Context context) {
        return isEVDO_0BySubtype(context) || isEVDO_ABySubtype(context) ||
                isEVDO_BBySubtype(context);
    }


    /**
     * 获取Wifi的状态，需要ACCESS_WIFI_STATE权限
     *
     * @param context 上下文
     * @return 取值为WifiManager中的WIFI_STATE_ENABLED、WIFI_STATE_ENABLING、WIFI_STATE_DISABLED、WIFI_STATE_DISABLING、WIFI_STATE_UNKNOWN之一
     * @throws Exception 没有找到wifi设备
     */
    public static int getWifiState(Context context) throws Exception {
        WifiManager wifiManager = ((WifiManager) context.getSystemService(
                Context.WIFI_SERVICE));
        if (wifiManager != null) {
            return wifiManager.getWifiState();
        }
        else {
            throw new Exception("wifi device not found!");
        }
    }


    /**
     * 判断Wifi是否打开，需要ACCESS_WIFI_STATE权限
     *
     * @param context 上下文
     * @return true：打开；false：关闭
     */
    public static boolean isWifiOpen(Context context) throws Exception {
        int wifiState = getWifiState(context);
        return wifiState == WifiManager.WIFI_STATE_ENABLED ||
                wifiState == WifiManager.WIFI_STATE_ENABLING
                ? true
                : false;
    }


    /**
     * 设置Wifi，需要CHANGE_WIFI_STATE权限
     *
     * @param context 上下文
     * @param enable wifi状态
     * @return 设置是否成功
     */
    public static boolean setWifi(Context context, boolean enable)
            throws Exception {
        //如果当前wifi的状态和要设置的状态不一样
        if (isWifiOpen(context) != enable) {
            ((WifiManager) context.getSystemService(
                    Context.WIFI_SERVICE)).setWifiEnabled(enable);
        }
        return true;
    }


    /**
     * 判断移动网络是否打开，需要ACCESS_NETWORK_STATE权限
     *
     * @param context 上下文
     * @return true：打开；false：关闭
     */
    public static boolean isMobileNetworkOpen(Context context) {
        return (((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE)).isConnected();
    }


    /**
     * 获取本机IP地址
     *
     * @return null：没有网络连接
     */
    public static String getIpAddress() {
        try {
            NetworkInterface nerworkInterface;
            InetAddress inetAddress;
            for (Enumeration<NetworkInterface> en
                 = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                nerworkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr
                     = nerworkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
            return null;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
