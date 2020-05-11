package com.pds.ndk;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Created by Administrator on 2017/11/6.
 */

public class DexManager {

  private Context context;
  private static final DexManager ourInstance = new DexManager();

  public static DexManager getInstance() {
    return ourInstance;
  }

  private DexManager() {
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void loadFile(File file) {
    try {
      DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(),
          new File(context.getCacheDir(), "opt").getAbsolutePath(), Context.MODE_PRIVATE);
      //下一步  得到class   ----取出修复好的Method
      Enumeration<String> entry= dexFile.entries();
      while (entry.hasMoreElements()) {
//                拿到全类名
        String className=entry.nextElement();
//                    Class.forName(className);//
        Class clazz=dexFile.loadClass(className, context.getClassLoader());
        if (clazz != null) {
          fixClazz(clazz);
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void fixClazz(Class realClazz) {
//        服务器修复好的  realClazz
    Method[] methods=realClazz.getDeclaredMethods();
    for (Method rightMethod : methods) {
      Replace replace = rightMethod.getAnnotation(Replace.class);
      if (replace == null) {
        continue;
      }
      //找到了修复好的Method    找到出bug的Method
      String wrongClazz=replace.clazz();
      String wrongMethodName=replace.method();
      try {
        Class clazz= Class.forName(wrongClazz);
        Method wrongMethod = clazz.getDeclaredMethod(wrongMethodName, rightMethod.getParameterTypes());
        if (Build.VERSION.SDK_INT <= 18) {
          androidFix(Build.VERSION.SDK_INT ,wrongMethod, rightMethod);
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }


    }
  }
  private native void androidFix(int sdk, Method wrongMethod, Method rightMethod);
  private native void androidArt(int sdk, Method wrongMethod, Method rightMethod);

}
