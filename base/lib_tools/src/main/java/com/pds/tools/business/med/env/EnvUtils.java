package com.pds.tools.business.med.env;

import android.webkit.URLUtil;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.pds.tools.common.cache.PreferencesKey;
import com.pds.tools.common.cache.PreferencesManager;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/2 2:42 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class EnvUtils {

    public static String getEnv(int envType) {
        boolean defaultEnvSwitch = PreferencesManager
                .getBoolean(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_ENV_SWITCH);
        String host = null;
        if (defaultEnvSwitch) {
            int env = PreferencesManager
                    .getInt(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_DEFAULT_ENV);
            host = getHost(envType, env);
        } else {
            String s = PreferencesManager
                    .getString(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_CUSTOM_ENV, "");
            if (isRegular(s)) {
                host = s;
            }
        }
        return host;
    }

    public static boolean isRegular(String url) {
        return null != url && (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url));
    }

    private static final int NOT_TYPE = -1;
    private static final String PATIENT_HOST = "patient-medication";
    private static final int PATIENT_TYPE = 1;

    private static final String WEB_HOST = "web";
    private static final int WEB_TYPE = 2;

    private static String getHost(int envType, int env) {
        switch (envType) {
            case PATIENT_TYPE:
                return PatientEnv.getAppEnv(env);
            case WEB_TYPE:
                return WebEnv.getWebEnv(env);
            default:
                return null;
        }
    }

    public static int envType(String originHost) {
        boolean all = PreferencesManager
                .getBoolean(DoraemonKit.APPLICATION, PreferencesKey.MED_NET_ALL_ENV, false);
        if (!all) {
            return NOT_TYPE;
        }
        return type(originHost);
    }

    public static int type(String originHost){
        if (originHost.startsWith(PATIENT_HOST)) {
            return PATIENT_TYPE;
        } else if (originHost.startsWith(WEB_HOST)) {
            return WEB_TYPE;
        } else {
            return NOT_TYPE;
        }
    }
}
