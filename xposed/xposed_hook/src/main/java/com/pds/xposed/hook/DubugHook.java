package com.pds.xposed.hook;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
/**
 * @author: pengdaosong
 * CreateTime:  2020-04-08 10:50
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class DubugHook implements IXposedHookLoadPackage {
    private static final String TAG = "DubugHook";

    private static final String HOOK_APP_PACKAGE_NAME = "net.medlinker.medlinker";

    private static boolean isBackup(ApplicationInfo paramApplicationInfo) {
        try {
            if ((0x8000 & paramApplicationInfo.flags) != 0) {
                Log.i(TAG, "Open Backup");
                return true;
            }
        } catch (Exception localException) {
            Log.i(TAG, "Close Backup");
        }
        return false;
    }

    public static boolean isDebugable(ApplicationInfo paramApplicationInfo) {
        try {
            if ((0x2 & paramApplicationInfo.flags) != 0) {
                Log.i(TAG, "Open Debugable");
                return true;
            }
        } catch (Exception localException) {
            Log.i(TAG, "Close Debugable");
        }
        return false;
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam paramLoadPackageParam) throws Throwable {
//        openDebugable(paramLoadPackageParam.classLoader);
        _360Firm(paramLoadPackageParam);
    }

    // 修改应用内调试变量
    private void modifyAppDebugVar(String packageName,ClassLoader classLoader){
        XposedBridge.log("modifyAppDebugVar hook");
        if (HOOK_APP_PACKAGE_NAME .equals(packageName)){
            XposedBridge.log("Main start hook");
            final Class<?> clazz = XposedHelpers.findClass("com.medlinker.base.BuildConfig",classLoader);
            XposedHelpers.setStaticBooleanField(clazz,"LOG_DEBUG",true);
            XposedHelpers.setStaticBooleanField(clazz,"DEBUG",true);
            XposedHelpers.setStaticObjectField(clazz,"BUILD_TYPE","release");

            final Class<?> clazz1 = XposedHelpers.findClass("com.medlinker.base.network.interceptor.OkHttpLogInterceptor",classLoader);

            Field[] fields = clazz1.getDeclaredFields();
            if (null == fields || fields.length < 1){
                XposedBridge.log("Main start fields is null");
                return;
            }
            for (Field f:fields) {
                String name = f.getName();
                XposedBridge.log("Main start f = " + name);
                if ("isShowFullLog".equals(name)){
                    try {
                        XposedBridge.log("Main start set true ");
                        f.setAccessible(true);
                        f.setBoolean(clazz1,true);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            XposedHelpers.setBooleanField(clazz1,"isShowFullLog",true);
        }
    }

    private void hookMethod(XC_LoadPackage.LoadPackageParam packageParam, final ClassLoader classLoader){
        XposedBridge.log("hookMethod hook");
        if (HOOK_APP_PACKAGE_NAME .equals(packageParam.packageName)){
            final Class<?> clazz = XposedHelpers.findClass("com.medlinker.base.network.RetrofitProvide",classLoader);
            final XC_MethodHook local1 = new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("hookMethod afterHookedMethod");
                    Object object =  param.getResult();
                    if (object != null) {
                        XposedBridge.log("hookMethod add interceptor");
                        Class<?>  clzz = object.getClass();
                        try {
                            Method newBuilder = clzz.getMethod("newBuilder");
                            if (null != newBuilder){

                                newBuilder.setAccessible(true);
                                Object client = newBuilder.invoke(object);
                                Class<?>  clzzClient= client.getClass();

                                final Class<?> para = XposedHelpers.findClass("okhttp3.Interceptor",classLoader);

                                Method method = clzzClient.getMethod("addNetworkInterceptor",para);
                                if (null != method){
                                    method.setAccessible(true);
                                    final Class<?> clazz1 = XposedHelpers.findClass("com.medlinker.base.network.interceptor.OkHttpLogInterceptor",classLoader);
                                    method.invoke(client,clazz1.newInstance());


                                    Method me = clzzClient.getMethod("build");
                                    param.setResult(me.invoke(client));
                                    final Class<?> clazz = XposedHelpers.findClass("com.heaven7.core.util.Logger",classLoader);
                                    XposedHelpers.setStaticIntField(clazz,"sLOG_LEVEL",6);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };
//            XposedHelpers.findAndHookMethod(clazz,"buildDefaultClient", Boolean.class,local1);
            XposedBridge.hookAllMethods(clazz, "buildDefaultClient", local1);
        }
    }

    /**
     * 通过360加固，获取真实的classLoader
     * @param packageParam
     */
    private void _360Firm(final XC_LoadPackage.LoadPackageParam packageParam){
        XposedBridge.log("_360Firm start");
        //hook 360壳
        XposedHelpers.findAndHookMethod("com.stub.StubApp", packageParam.classLoader,"getOrigApplicationContext", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("_360Firm afterHookedMethod");
                //获取到360的Context对象，通过这个对象来获取classloader
                Context context = (Context) param.args[0];
                //获取360的classloader，之后hook加固后的代码就使用这个classloader
                ClassLoader classLoader =context.getClassLoader();
                //替换classloader,hook加固后的真正代码
//                modifyAppDebugVar(packageParam.packageName,classLoader);
                hookMethod(packageParam,classLoader);
            }
        });
    }

    /**
     * xposed开源modle实现：https://security.tencent.com/index.php/opensource/detail/17
     * @param classLoader
     */
    private void openDebugable(ClassLoader classLoader){
        XC_MethodHook local1 = new XC_MethodHook() {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                PackageInfo localPackageInfo = (PackageInfo) paramAnonymousMethodHookParam.getResult();
                if (localPackageInfo != null) {
                    ApplicationInfo localApplicationInfo = localPackageInfo.applicationInfo;
                    int i = localApplicationInfo.flags;
                    Log.i(TAG, "Load App : " + localApplicationInfo.packageName);
                    Log.i(TAG, "==== After Hook ====");
                    if ((i & 0x8000) == 0) {
                        i |= 0x8000;
                    }
                    if ((i & 0x2) == 0) {
                        i |= 0x2;
                    }
                    localApplicationInfo.flags = i;
                    paramAnonymousMethodHookParam.setResult(localPackageInfo);
                    Log.i(TAG, "flags = " + i);
                    DubugHook.isDebugable(localApplicationInfo);
                    DubugHook.isBackup(localApplicationInfo);
                }
            }
        };
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.pm.PackageManagerService", classLoader), "getPackageInfo", local1);
    }
}
