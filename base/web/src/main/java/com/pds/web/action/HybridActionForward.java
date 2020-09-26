package com.pds.web.action;

import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.web.param.HybridParamForward;
import com.pds.web.util.ActivityUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HybridActionForward extends HybridAction {

    private static final String TAG = HybridActionForward.class.getSimpleName();

    @Override
    public void onAction(Context context, int webviewHashCode, String params, String jsmethod) {
        HybridParamForward hybridParam = mGson.fromJson(params, HybridParamForward.class);
//        LogUtil.d(TAG, "onAction type =  %s, toPage = %s, hasNavigation = %b", hybridParam.type.mValue, hybridParam.topage, hybridParam.hasnavgation);
        switch (hybridParam.type) {
            case NATIVE:
                try {
                    final String toPageUrl = hybridParam.topage;
                    if (toPageUrl.contains("?") && (toPageUrl.contains("http://") || toPageUrl.contains("https://"))) {
                        //参数包含http且没有encode，必须URLEncode处理。
                        final int beginIndex = toPageUrl.indexOf("?");
                        String part1 = hybridParam.topage.substring(0, beginIndex);
                        final String paramStr = toPageUrl.substring(beginIndex);
                        String[] keyValues = paramStr.split("&");
                        Map<String, String> map = new HashMap<>();
                        for (String keyValue : keyValues) {
                            final int index = keyValue.indexOf("=");
                            String key = keyValue.substring(0, index);
                            String value = keyValue.substring(index);
                            if (value.contains("http")) {
                                value = Uri.encode(value);
                            }
                            map.put(key, value);
                        }
                        StringBuilder sBuilder = new StringBuilder(part1 + "?");
                        Iterator<String> iterator = map.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            sBuilder.append(key).append("=")
                                    .append(map.get(key))
                                    .append("&");
                        }
                        sBuilder.substring(0, sBuilder.lastIndexOf("&"));
                        hybridParam.topage = sBuilder.toString();
//                        LogUtil.d(TAG, "onAction type = %s, after Encode  toPage = %s", hybridParam.type.mValue, hybridParam.topage);
                    }
                    ARouter.getInstance().build(hybridParam.topage).navigation(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case H5:
                ActivityUtil.forwardHybridH5(context, hybridParam.topage, hybridParam.animate, hybridParam.hasnavgation);
                break;
            default:
        }
    }

}
