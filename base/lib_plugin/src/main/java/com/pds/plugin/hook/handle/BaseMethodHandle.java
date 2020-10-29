package com.pds.plugin.hook.handle;

import android.content.Context;
import android.os.RemoteException;

import com.pds.plugin.pm.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseMethodHandle  {

    protected Context mHostContext;

    private Object userMyResult;

    public void setUserMyResult(Object userMyResult) {
        this.userMyResult = userMyResult;
    }

    public BaseMethodHandle(Context mHostContext) {
        this.mHostContext = mHostContext;
    }

    //   (前 ) hook  （hook后）
//invoke  ---->doHookInnner   ---->beforeInvoke ()
    public Object doHookInnner(Object receiver, Method method, Object[] args) {
        userMyResult = null;

// userMyResult  不为空

        Object invokeResult = null;
        try {
            boolean suc =beforeInvoke(receiver, method, args);
            if (!suc) {
                invokeResult=  method.invoke(receiver, args);
            }
            afterInvoke(receiver, method, args,invokeResult);
            if (userMyResult != null) {
                return userMyResult;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return invokeResult;
    }

    /**
     * 在某个方法被调用之前执行，如果返回true，则不执行原始的方法，否则执行原始方法
     */
    protected boolean beforeInvoke(Object receiver, Method method, Object[] args)  throws Throwable  {
        return false;
    }

    protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable    {
    }


    private static boolean isPackagePlugin(String packageName) throws RemoteException {
        return PluginManager.getInstance().isPluginPackage(packageName);
    }
}