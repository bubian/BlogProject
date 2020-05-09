package com.pds.util.device;

import java.io.File;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-15 16:56
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class RootCheckUtils {
    /**
     * 检查root权限
     *
     * @return
     */
    public boolean isRoot() {
        int secureProp = getroSecureProp();
        if (secureProp == 0)//eng/userdebug版本，自带root权限
            return true;
        else return isSUExist();//user版本，继续查su文件
    }

    private int getroSecureProp() {
        int secureProp;
        String roSecureObj = getProperty("ro.secure");
        if (roSecureObj == null) secureProp = 1;
        else {
            if ("0".equals(roSecureObj)) secureProp = 0;
            else secureProp = 1;
        }
        return secureProp;
    }


    private boolean isSUExist() {
        File file;
        String[] paths = {"/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};
        for (String path : paths) {
            file = new File(path);
            if (file.exists()) return true;
        }
        return false;
    }

    private String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Exception e) {
            value = null;
        } finally {
            return value;
        }
    }
}
