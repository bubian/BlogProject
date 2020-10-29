/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.pds.plugin.am;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
class StaticProcessList {

    private static final String CATEGORY_ACTIVITY_PROXY_STUB = "com.pds.plugin.category.PROXY_STUB";
    private static final String TAG = "StaticProcessList";

    //key=processName value=ProcessItem
    private Map<String, ProcessItem> items = new HashMap<String, ProcessItem>(10);

    private List<String> mOtherProcessNames = new ArrayList<>();


    /**
     * 我们预注册的进程item
     * <p/>
     * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/10.
     */
    private class ProcessItem {

        private String name;

        //key=ActivityInfo.name,value=ActivityInfo
        private Map<String, ActivityInfo> activityInfos = new HashMap<String, ActivityInfo>(4);
        //key=ServiceInfo.name,value=ServiceInfo
        private Map<String, ServiceInfo> serviceInfos = new HashMap<String, ServiceInfo>(1);
        //key=ProviderInfo.authority,value=ProviderInfo
        private Map<String, ProviderInfo> providerInfos = new HashMap<String, ProviderInfo>(1);

        private void addActivityInfo(ActivityInfo info) {
            Log.i(TAG, "addActivityInfo: ");
            if (!activityInfos.containsKey(info.name)) {
                activityInfos.put(info.name, info);
            }
        }


        private void addServiceInfo(ServiceInfo info) {
            Log.i(TAG, "addServiceInfo: ");
            if (!serviceInfos.containsKey(info.name)) {
                serviceInfos.put(info.name, info);
            }
        }


        private void addProviderInfo(ProviderInfo info) {
            Log.i(TAG, "addProviderInfo: ");
            if (!providerInfos.containsKey(info.authority)) {
                providerInfos.put(info.authority, info);
            }
        }
    }


    void onCreate(Context mHostContext) throws NameNotFoundException {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(CATEGORY_ACTIVITY_PROXY_STUB);
        intent.setPackage(mHostContext.getPackageName());


        PackageManager pm = mHostContext.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        for (ResolveInfo activity : activities) {
            addActivityInfo(activity.activityInfo);
        }

        List<ResolveInfo> services = pm.queryIntentServices(intent, 0);
        for (ResolveInfo service : services) {
            addServiceInfo(service.serviceInfo);
        }

        PackageInfo packageInfo = pm.getPackageInfo(mHostContext.getPackageName(), PackageManager.GET_PROVIDERS);
        if (packageInfo.providers != null && packageInfo.providers.length > 0) {
            for (ProviderInfo providerInfo : packageInfo.providers) {
                if (providerInfo.name != null) {
                    addProviderInfo(providerInfo);
                }
            }
        }

        mOtherProcessNames.clear();
        PackageInfo packageInfo1 = pm.getPackageInfo(mHostContext.getPackageName(), PackageManager.GET_ACTIVITIES
                | PackageManager.GET_RECEIVERS
                | PackageManager.GET_PROVIDERS
                | PackageManager.GET_SERVICES);
        if (packageInfo1.activities != null) {
            for (ActivityInfo info : packageInfo1.activities) {
                if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                    mOtherProcessNames.add(info.processName);
                }
            }
        }

        if (packageInfo1.receivers != null) {
            for (ActivityInfo info : packageInfo1.receivers) {
                if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                    mOtherProcessNames.add(info.processName);
                }
            }
        }

        if (packageInfo1.providers != null) {
            for (ProviderInfo info : packageInfo1.providers) {
                if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                    mOtherProcessNames.add(info.processName);
                }
            }
        }

        if (packageInfo1.services != null) {
            for (ServiceInfo info : packageInfo1.services) {
                if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                    mOtherProcessNames.add(info.processName);
                }
            }
        }
    }

    public List<String> getOtherProcessNames() {
        return mOtherProcessNames;
    }

    private void addActivityInfo(ActivityInfo info) {
        Log.i(TAG, "addActivityInfo: ");
        if (TextUtils.isEmpty(info.processName)) {
            info.processName = info.packageName;
        }
        ProcessItem item = items.get(info.processName);
        if (item == null) {
            item = new ProcessItem();
            item.name = info.processName;
            items.put(info.processName, item);
        }
        item.addActivityInfo(info);
    }

    ActivityInfo findActivityInfoForName(String processName, String activityName) {
        Log.i(TAG, "findActivityInfoForName: ");
        ProcessItem item = items.get(processName);
        if (item != null && item.activityInfos != null) {
            return item.activityInfos.get(activityName);
        }
        return null;
    }

    ActivityInfo findActivityInfoForLaunchMode(String processName, int launchMode) {
        Log.i(TAG, "findActivityInfoForLaunchMode: ");
        ProcessItem item = items.get(processName);
        if (item != null && item.activityInfos != null) {
            for (ActivityInfo info : item.activityInfos.values()) {
                if (info.launchMode == launchMode) {
                    return info;
                }
            }
        }
        return null;
    }


    private void addServiceInfo(ServiceInfo info) {
        Log.i(TAG, "addServiceInfo: ");
        if (TextUtils.isEmpty(info.processName)) {
            info.processName = info.packageName;
        }
        ProcessItem item = items.get(info.processName);
        if (item == null) {
            item = new ProcessItem();
            item.name = info.processName;
            items.put(info.processName, item);
        }
        item.addServiceInfo(info);
    }

    ServiceInfo findServiceInfoForName(String processName, String serviceInfoName) {
        Log.i(TAG, "findServiceInfoForName: ");
        ProcessItem item = items.get(processName);
        if (item != null && item.serviceInfos != null) {
            return item.serviceInfos.get(serviceInfoName);
        }
        return null;
    }


    private void addProviderInfo(ProviderInfo info) {
        Log.i(TAG, "addProviderInfo: ");
        if (TextUtils.isEmpty(info.processName)) {
            info.processName = info.packageName;
        }
        ProcessItem item = items.get(info.processName);
        if (item == null) {
            item = new ProcessItem();
            item.name = info.processName;
            items.put(info.processName, item);
        }
        item.addProviderInfo(info);
    }

    ProviderInfo findProviderInfoForAuthority(String processName, String authority) {
        Log.i(TAG, "findProviderInfoForAuthority: ");
        ProcessItem item = items.get(processName);
        if (item != null && item.providerInfos != null) {
            return item.providerInfos.get(authority);
        }
        return null;
    }

    List<String> getProcessNames() {
        Log.i(TAG, "getProcessNames: ");
        return new ArrayList<String>(items.keySet());
    }

    List<ActivityInfo> getActivityInfoForProcessName(String processName) {
        Log.i(TAG, "getActivityInfoForProcessName: ");
        ProcessItem item = items.get(processName);
        ArrayList<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>(item.activityInfos.values());
        Collections.sort(activityInfos, sComponentInfoComparator);
        return activityInfos;
    }


    private static final Comparator<ComponentInfo> sComponentInfoComparator = new Comparator<ComponentInfo>() {
        @Override
        public int compare(ComponentInfo lhs, ComponentInfo rhs) {
            return Collator.getInstance().compare(lhs.name, rhs.name);
        }
    };


    List<ServiceInfo> getServiceInfoForProcessName(String processName) {
        Log.i(TAG, "getServiceInfoForProcessName: ");
        ProcessItem item = items.get(processName);
        ArrayList<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>(item.serviceInfos.values());
        Collections.sort(serviceInfos, sComponentInfoComparator);
        return serviceInfos;
    }

    List<ProviderInfo> getProviderInfoForProcessName(String processName) {
        Log.i(TAG, "getProviderInfoForProcessName: ");
        ProcessItem item = items.get(processName);
        ArrayList<ProviderInfo> providerInfos = new ArrayList<ProviderInfo>(item.providerInfos.values());
        Collections.sort(providerInfos, sComponentInfoComparator);
        return providerInfos;
    }

    void clear() {
        items.clear();
    }
}
