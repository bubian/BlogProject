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

package com.pds.plugin.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import java.util.List;

interface IPluginManager {

     //for my api
     boolean waitForReady();

     //////////////////////////////////////
     //
     //  THIS API FOR PACKAGE MANAGER
     //
     //////////////////////////////////////

     PackageInfo getPackageInfo(in String packageName, int flags);

     boolean isPluginPackage(in String packageName);

     ActivityInfo getActivityInfo(in ComponentName className, int flags);





     ResolveInfo resolveIntent(in Intent intent, in String resolvedType, int flags);

     List<ResolveInfo> queryIntentActivities(in Intent intent,in  String resolvedType, int flags);



      void onActivityCreated(in ActivityInfo stubInfo,in ActivityInfo targetInfo);


     List<PackageInfo> getInstalledPackages(int flags);












     ApplicationInfo getApplicationInfo(in String packageName, int flags);

     int installPackage(in String filepath,int flags);

     int deletePackage(in String packageName ,int flags);

     List<ActivityInfo> getReceivers(in String packageName ,int flags);

     List<IntentFilter> getReceiverIntentFilter(in ActivityInfo info);


      //////////////////////////////////////
      //
      //  THIS API FOR ACTIVITY MANAGER
      //
      //////////////////////////////////////

      ActivityInfo selectStubActivityInfo(in ActivityInfo targetInfo);
      ActivityInfo selectStubActivityInfoByIntent(in Intent targetIntent);




      String getProcessNameByPid(in int pid);





      void onActivityDestory(in ActivityInfo stubInfo,in ActivityInfo targetInfo);




      void reportMyProcessName(in String stubProcessName,in String targetProcessName, String targetPkg);

}
