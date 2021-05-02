package com.pds.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.pds.ui.ModuleUI;

import java.lang.reflect.Field;

/**
 * Author: KindyFung.
 * CreateTime:  2015/10/27 18:31
 * Email：fangjing@medlinker.com.
 * Description: Toast工具类
 */
public class ToastUtil {
    public static boolean isReadToastShow = false;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final String msg) {
        showMessage(null, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final String msg) {
        showMessage(null, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息
     */
    public static void showMessage(final Context act, final int msg,
                                   final int len) {
        try {
            showMessage(act, act.getResources().getString(msg), len);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toast发送消息
     */
    public static void showMessage(final Context act, final String msg,
                                   final int len) {
        handler.post(() -> {
            if (toast != null) {
                toast.setText(msg);
                toast.setDuration(len);
            } else {
                toast = Toast.makeText(act == null ? ModuleUI.instance().appContext() : act.getApplicationContext(), msg, len);
            }
            hook(toast);
            toast.show();
        });
    }

    private static Field sField_TN ;
    private static Field sField_TN_Handler ;
    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {}
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler)sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn,new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {}
    }

    private static class SafelyHandlerWarpper extends Handler {

        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {}
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }

}

