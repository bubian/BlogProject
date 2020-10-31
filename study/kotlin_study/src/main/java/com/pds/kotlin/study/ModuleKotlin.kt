package com.pds.kotlin.study

import android.app.Application
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import xcrash.XCrash
import java.io.File

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 11:58 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class ModuleKotlin {
    var mModuleStorage: ModuleKotlin? = null
    private var mApplication: Application? = null

    private object Lazy {
        val INSTANCE = ModuleKotlin()
    }

    companion object {
        @JvmStatic
        fun instance(): ModuleKotlin {
            return Lazy.INSTANCE
        }
    }

    /**
     * 不一定要在启动的时候初始化，根据业务而定
     * @param application
     */
    public fun init(application: Application) {
        mApplication = application
        initToolsLib()
        // xCrash 能为安卓 app 提供捕获 java 崩溃，native 崩溃和 ANR 的能力。不需要 root 权限或任何系统权限。（https://github.com/iqiyi/xCrash）
        // 日志保存目录：/data/data/PACKAGE_NAME/files/tombstones
        XCrash.init(application)
        // logan:美团日志上报库
        initLogan()
    }


    /**
     * 工具库
     */
    private fun initToolsLib() {
        try {
            val clazz = Class.forName("com.pds.tools.ModuleTools")
            val method = clazz.getMethod("init", Application::class.java)
            method.isAccessible = true
            method.invoke(clazz, this)
        } catch (ignored: Exception) {
        }
    }

    /**
     * https://github.com/Meituan-Dianping/Logan/tree/master/Example/Logan-Android
     * https://tech.meituan.com/2018/10/11/logan-open-source.html
     */
    private fun initLogan() {
        val config = LoganConfig.Builder()
            .setCachePath(appContext().filesDir.absolutePath)
            .setPath((mApplication?.cacheDir?.absolutePath + File.separator) + "logan_v1")
            .setEncryptKey16("0123456789012345".toByteArray())
            .setEncryptIV16("0123456789012345".toByteArray())
            .build()
        Logan.init(config)
    }

    fun appContext(): Application {
        if (null == mApplication) {
            mApplication = findApplicationFromApp()
        }
        if (null == mApplication) {
            mApplication = findApplicationFromSystem()
        }
        if (null == mApplication) {
            throw NullPointerException("ModuleStorage appContext is null")
        }
        return mApplication as Application
    }

    private fun findApplicationFromApp(): Application? {
        try {
            val clazz = Class.forName(BuildConfig.APPLICATION_CLASS_NAME)
            val field = clazz.getField(BuildConfig.APPLICATION_VAR_NAME)
            field.isAccessible = true
            val app = field[clazz]
            if (app is Application) {
                return app
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    private fun findApplicationFromSystem(): Application? {
        try {
            val method =
                Class.forName("android.app.ActivityThread").getMethod("currentActivityThread")
            method.isAccessible = true
            val at = method.invoke(null)
            val app = at.javaClass.getMethod("getApplication").invoke(at)
            if (app is Application) {
                return app
            }
        } catch (ignored: Exception) {
        }
        return null
    }
}