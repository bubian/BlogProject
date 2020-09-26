package com.pds.web.core;

import com.pds.web.R;
import com.pds.web.action.HybridActionAjaxGet;
import com.pds.web.action.HybridActionAjaxPost;
import com.pds.web.action.HybridActionAliPay;
import com.pds.web.action.HybridActionBack;
import com.pds.web.action.HybridActionDevice;
import com.pds.web.action.HybridActionForward;
import com.pds.web.action.HybridActionGetDevInfo;
import com.pds.web.action.HybridActionLogin;
import com.pds.web.action.HybridActionOnMsgReceived;
import com.pds.web.action.HybridActionOpenLink;
import com.pds.web.action.HybridActionPop;
import com.pds.web.action.HybridActionPopup;
import com.pds.web.action.HybridActionRefresh;
import com.pds.web.action.HybridActionSetShareData;
import com.pds.web.action.HybridActionShare;
import com.pds.web.action.HybridActionShowKeyboard;
import com.pds.web.action.HybridActionShowLoading;
import com.pds.web.action.HybridActionUpdateApp;
import com.pds.web.action.HybridActionUpdateHeader;
import com.pds.web.action.HybridActionUpload;
import com.pds.web.action.HybridActionWXPay;
import com.pds.web.action.HybridActionWebViewHide;
import com.pds.web.action.HybridActionWebViewShow;
import com.pds.web.action.HybridActionedScreenshot;

import java.util.HashMap;

/**
 * @author hmy
 * @time 2020/9/18 11:05
 */
public class HybridConfig {
    public static final String SCHEME = "medmedlinkerhybrid";
    public static final String FILE_HYBRID_DATA_VERSION = "hybrid_data_version";
    public static final String FILE_HYBRID_DATA_PATH = "hybrid_webapp";
    public static final String JSInterface = "HybridJSInterface";

    public static class TagnameMapping {
        private static HashMap<String, Class> mMap;

        static {
            mMap = new HashMap<>();
            mMap.put("updateheader", HybridActionUpdateHeader.class);
            mMap.put("back", HybridActionBack.class);
            mMap.put("forward", HybridActionForward.class);
            mMap.put("pop", HybridActionPop.class);
            mMap.put("loading", HybridActionShowLoading.class);
            mMap.put("getdevicenum", HybridActionDevice.class);
            mMap.put("uploadImage", HybridActionUpload.class);
            mMap.put("login", HybridActionLogin.class);
            mMap.put("get", HybridActionAjaxGet.class);
            mMap.put("post", HybridActionAjaxPost.class);
            mMap.put("headerrefresh", HybridActionRefresh.class);
            mMap.put("paybyalipay", HybridActionAliPay.class);
            mMap.put("paybywxpay", HybridActionWXPay.class);
            mMap.put("onwebviewshow", HybridActionWebViewShow.class);
            mMap.put("onwebviewhide", HybridActionWebViewHide.class);

            mMap.put("activitypopup", HybridActionPopup.class);
            mMap.put("onmsgreceived", HybridActionOnMsgReceived.class);
            mMap.put("share", HybridActionShare.class);
            mMap.put("setShareData", HybridActionSetShareData.class);
            mMap.put("updateapp", HybridActionUpdateApp.class);
            mMap.put("openLink", HybridActionOpenLink.class);
            mMap.put("getdeviceinfo", HybridActionGetDevInfo.class);
            mMap.put("medScreenshot", HybridActionedScreenshot.class);
            mMap.put("showKeyboard", HybridActionShowKeyboard.class);
        }

        public static void appendMapping(String tagname, Class clazz) {
            mMap.put(tagname, clazz);
        }

        public static Class mapping(String method) {
            return mMap.get(method);
        }
    }

    public static class IconMapping {
        private static HashMap<String, Integer> mMap;

        static {
            mMap = new HashMap<>();
            mMap.put("back", R.mipmap.icon_naviback_gray);
            mMap.put("search", R.drawable.hybrid_icon_search);
//            mMap.put("share", net.medlinker.medlinker.R.mipmap.icon_more_white_normal);
        }

        public static int mapping(String icon) {
            boolean has = mMap.containsKey(icon);
            if (!has) {
                return -1;
            }
            return mMap.get(icon);
        }
    }
}
