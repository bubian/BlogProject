package com.pds.util.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.pds.util.net.NetworkUtil;
import com.pds.util.system.SystemPropProxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by bin.jing on 2015/12/21.
 */
public class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getSimpleName();
    private static final String STB_SERIAL_NUM = "sys.smartcard.id";
    ;
    private static String _mac = "";
    private static String _deviceId = "";

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @param context 上下文
     * @return MAC地址
     */
    public static String getDeviceMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String macAddress = info.getMacAddress().replace(":", "");
        return macAddress == null ? "" : macAddress;
    }

    /**
     * 获取设备MAC地址
     *
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return MAC地址
     */
    public static String getDeviceMac() {
        String macAddress = null;
        LineNumberReader reader = null;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            reader = new LineNumberReader(ir);
            macAddress = reader.readLine().replace(":", "");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return macAddress == null ? "" : macAddress;
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getMac() {
        if (!TextUtils.isEmpty(_mac) && 17 == _mac.length()) {
            return _mac;
        }
        _mac = getDeviceMac();
        if (TextUtils.isEmpty(_mac)){
            _mac = NetworkUtil.getLANMac();
        }
        if (TextUtils.isEmpty(_mac)) {
            _mac = NetworkUtil.getWifiMac();
        }
        Log.i(TAG, "getMac mac:" + _mac);
        if (TextUtils.isEmpty(_mac)) {
            Log.e(TAG, "getMac failed!!!");
            return "";
        }
        return _mac;
    }

    public static String getSmartCardId() {
        return "";
    }

    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(_deviceId)) {
            _deviceId = getDeviceIdFromSystem(context);
        }
        return _deviceId;
    }

    public static final String getDefaultUserId() {
        return buildDefaultUserId();
    }

    private static String buildDefaultUserId() {
        return "u" + getMac().replace(":", "").replace("-", "").toLowerCase(Locale.CHINA);
    }

    public static String getDeviceIdFromSystem(Context context) {
        try {
            String deviceId = SystemPropProxy.get(context, STB_SERIAL_NUM, "");
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
        } catch (Exception e) {
        }
        return getMac().replace("-", "").toLowerCase(Locale.CHINA);
    }

    /**
     * 根据网卡名字获取网卡MAC地址
     * @param macName 要获取的网络的名字
     * @return
     */

    public static String getNetworkCardMac(String macName){
        String macStr = "#";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface info : interfaces){
                if (!macName.equals(info.getName())){
                    continue;
                }
                byte[] mac = info.getHardwareAddress();
                if (null == mac){
                    macStr = "#";
                }else {
                    StringBuilder buf = new StringBuilder();
                    for (int idx=0; idx<mac.length; idx++){
                        buf.append(String.format("%02X:", mac[idx]));
                    }
                    if (buf.length()>0) {
                        buf.deleteCharAt(buf.length()-1);
                    }
                    macStr = buf.toString();
                }

                Log.d(TAG,"Network Card Mac:"+"name:"+info.getName()
                        +" mac:"+macStr +" index"+info.getIndex());

                if (macStr.length() < 17){
                    macStr = "#";
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return macStr ;
    }

    /**
     * 获取设备型号，如MI2SC
     *
     * @return 设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }


    /**
     * 获取设备厂商，如Xiaomi
     *
     * @return 设备厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     *
     * @param context  上下文
     * @return  apn
     */
    public static String getAPN(Context context) {

        String apn = "";
        ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null) {
            if (ConnectivityManager.TYPE_WIFI == info.getType()) {
                apn = info.getTypeName();
                if (apn == null) {
                    apn = "wifi";
                }
            }
            else {
                apn = info.getExtraInfo().toLowerCase();
                if (apn == null) {
                    apn = "mobile";
                }
            }
        }
        return apn;
    }


    /**
     *
     * @return  获取语言
     */
    public static String getLanguage() {

        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = "";
        }
        return languageCode;
    }


    /**
     *
     * @return  获取国家
     */
    public static String getCountry() {

        Locale locale = Locale.getDefault();
        String countryCode = locale.getCountry();
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = "";
        }
        return countryCode;
    }

    /**
     *
     * @param context   context
     * @return  imei
     */
    public static String getIMEI(Context context) {

        TelephonyManager mTelephonyMgr
                = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        if (TextUtils.isEmpty(imei) || imei.equals("000000000000000")) {
            imei = "0";
        }

        return imei;
    }

    /**
     *
     * @param context  context
     * @return  imsi
     */
    public static String getIMSI(Context context) {

        TelephonyManager mTelephonyMgr
                = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        if (TextUtils.isEmpty(imsi)) {
            return "0";
        }
        else {
            return imsi;
        }
    }

    /**
     *
     * @param context  context
     * @return  mcnc
     */
    public static String getMcnc(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String mcnc = tm.getNetworkOperator();
        if (TextUtils.isEmpty(mcnc)) {
            return "0";
        }
        else {
            return mcnc;
        }
    }


}