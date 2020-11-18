package com.pds.splugin.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.pds.splugin.ModuleSPlugin;
import com.pds.splugin.proxy.ProxyReplaceActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressLint("PrivateApi")
public class HookActivityReplace {
    private static final String TAG = "HookUtil";

    /**
     * Activity检测通过后（即AndroidManifest检测），发handler消息，进入加载Activity流程
     */
    private static final int MH_LOAD_ACTIVITY = 100;

    public static void init() {
        // hookStartActivity();
        hookHookMh();
    }

    /**
     * hook系统mh，Activity检测通过后替换成真正需要启动的Activity
     */
    public static void hookHookMh() {
        try {
            Log.i(TAG, "-----------------hookHookMh");
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = forName.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            // 还原系统的ActivityTread   mH
            Object activityThreadObj = currentActivityThreadField.get(null);
            Field handlerField = forName.getDeclaredField("mH");
            handlerField.setAccessible(true);
            // hook点找到了
            Handler mH = (Handler) handlerField.get(activityThreadObj);
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(mH, new ActivityMH(mH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里把我们需要启动的Activity替换成代理Activity，以绕过检测
     * 这里不同系统版本，可能写法不同，所以如果反射不成功请查看源码后，做相应修改
     */
    public static void hookStartActivity() {
        // 还原 gDefault 成员变量  反射  调用一次
        try {
            Log.i(TAG, "-----------------hookStartActivity");
            Class<?> ActivityManagerNativeCls = Class.forName("android.app.ActivityManagerNative");
            Field gDefault = ActivityManagerNativeCls.getDeclaredField("gDefault");
            gDefault.setAccessible(true);
            // 因为是静态变量  所以获取的到的是系统值  hook   伪hook
            Object defaultValue = gDefault.get(null);
            //mInstance对象
            Class<?> SingletonClass = Class.forName("android.util.Singleton");
            Field mInstance = SingletonClass.getDeclaredField("mInstance");
            // 还原 IActivityManager对象  系统对象
            mInstance.setAccessible(true);
            Object iActivityManagerObject = mInstance.get(defaultValue);
            Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");
            MyProxyActivity startActivityMethod = new MyProxyActivity(iActivityManagerObject);
            // 第二参数  是即将返回的对象 需要实现那些接口
            Object oldIActivityManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                    , new Class[]{IActivityManagerIntercept, View.OnClickListener.class}
                    , startActivityMethod);
            // 将系统的iActivityManager  替换成    自己通过动态代理实现的对象   oldIActivityManager对象  实现了 IActivityManager这个接口的所有方法
            mInstance.set(defaultValue, oldIActivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ActivityMH implements Handler.Callback {
        private Handler mH;

        public ActivityMH(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(Message msg) {
            Log.i(TAG, "-----------------handleMessage" + msg.what);
            if (msg.what == MH_LOAD_ACTIVITY) {
                try {
                    Object obj = msg.obj;
                    Field intentField = obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent reallyIntent = (Intent) intentField.get(obj);
                    Intent oldIntent = reallyIntent.getParcelableExtra("oldIntent");
                    if (null == oldIntent) {
                        handleLaunchActivity(reallyIntent);
                    } else {
                        handleLaunchActivity(oldIntent, reallyIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 做了真正的跳转
            mH.handleMessage(msg);
            return true;
        }

        /**
         * 启动Activity前后Hook
         * @param oldIntent
         * @param reallyIntent
         */
        private void handleLaunchActivity(Intent oldIntent, Intent reallyIntent) {
            Log.i(TAG, "-----------------handleLaunchActivity");
            try {
                if (oldIntent != null) {
                    String classNameStr = oldIntent.getStringExtra("className");
                    if (TextUtils.isEmpty(classNameStr)) {
                        return;
                    }
                    Class<?> clazz = Class.forName(classNameStr);
                    if (!Activity.class.isAssignableFrom(clazz)) {
                        return;
                    }
                    ComponentName componentName = new ComponentName(ModuleSPlugin.instance().appContext(), clazz);
                    reallyIntent.putExtra("extraIntent", oldIntent.getComponent().getClassName());
                    reallyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    reallyIntent.setComponent(componentName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 直接启动代理Activity，等检测通过后，替换成真正的Activity
         * @param reallyIntent
         */
        private void handleLaunchActivity(Intent reallyIntent) {
            Log.i(TAG, "-----------------handleLaunchActivity:really");
            try {
                if (reallyIntent != null) {
                    String classNameStr = reallyIntent.getStringExtra("className");
                    Log.i(TAG, "-----------------handleLaunchActivity:" + classNameStr);
                    if (TextUtils.isEmpty(classNameStr)) {
                        return;
                    }
                    Class<?> clazz = Class.forName(classNameStr);
                    if (!Activity.class.isAssignableFrom(clazz)) {
                        return;
                    }
                    ComponentName componentName = new ComponentName(ModuleSPlugin.instance().appContext(), clazz);
                    reallyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    reallyIntent.setComponent(componentName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class MyProxyActivity implements InvocationHandler {
        private static final String TAG = "MyProxyActivity";
        private Object iActivityManagerObject;

        public MyProxyActivity(Object iActivityManagerObject) {
            this.iActivityManagerObject = iActivityManagerObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("startActivity".equals(method.getName())) {
                Log.i(TAG, "-----------------startActivity--------------------------");
                Intent intent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof Intent) {
                        intent = (Intent) args[i];
                        index = i;
                    }
                }
                Intent newIntent = new Intent();
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = new ComponentName(ModuleSPlugin.instance().appContext(), ProxyReplaceActivity.class);
                newIntent.setComponent(componentName);
                newIntent.putExtra("oldIntent", intent);
                args[index] = newIntent;
            }
            return method.invoke(iActivityManagerObject, args);
        }
    }

}
