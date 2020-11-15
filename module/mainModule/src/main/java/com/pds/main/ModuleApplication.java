package com.pds.main;

import android.app.Application;
import android.content.Context;

import com.pds.flutter.ModuleFlutter;
import com.pds.kotlin.study.ModuleKotlin;
import com.pds.log.core.Lg;
import com.pds.rn.ModuleRn;
import com.pds.router.ModuleRouter;
import com.pds.sample.application.ModuleSample;
import com.pds.splugin.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/26 2:31 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ModuleApplication {

    private static final String TAG = "MainApplication";
    private static ModuleApplication sModuleApplication = new ModuleApplication();
    private Application mApplication;

    private ModuleApplication() {
    }

    public static ModuleApplication instance() {
        return sModuleApplication;
    }

    public void attachBaseContext(Context base) {
        ModuleSample.instance().attachBaseContext(base);
    }

    public void onCreate(Application application) {
        mApplication = application;
        ModuleRouter.init(application);
        ModuleRn.init(application);
        ModuleFlutter.init(application);
        ModuleSample.instance().onCreate(application);
        ModuleKotlin.instance().init(application);
        initPlugin(application);

    }

    private void initPlugin(Application application) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            String name = "phonequery.apk";
            is = application.getAssets().open(name);
            Lg.d(TAG, "11111");
            File filePath = application.getDir("plugin", Context.MODE_PRIVATE);
            Lg.d(TAG, "22222");
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            File file = new File(filePath, name);
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            File f = new File(file.getAbsolutePath());
            if (f.exists()) {
                Lg.d(TAG, "dex file exists");
            }
            PluginManager.getInstance().loadPath(application, name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
