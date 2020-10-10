package com.pds.kotlin

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import com.pds.kotlin.study.dagger.ApplicationComponent
import com.pds.kotlin.study.dagger.DaggerApplicationComponent
import com.pds.log.core.Lg
import xcrash.XCrash
import java.io.File


/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 11:18
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class BaseApplication : MultiDexApplication() {

    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

    companion object {
        private const val TAG = "BaseApplication"
        private lateinit var application: BaseApplication

        @JvmStatic
        fun app(): BaseApplication {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        Lg.d(TAG, "onCreate")
        initToolsLib()
        // xCrash 能为安卓 app 提供捕获 java 崩溃，native 崩溃和 ANR 的能力。不需要 root 权限或任何系统权限。（https://github.com/iqiyi/xCrash）
        // 日志保存目录：/data/data/PACKAGE_NAME/files/tombstones
        XCrash.init(this)
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
            .setCachePath(applicationContext.filesDir.absolutePath)
            .setPath((cacheDir.absolutePath + File.separator) + "logan_v1")
            .setEncryptKey16("0123456789012345".toByteArray())
            .setEncryptIV16("0123456789012345".toByteArray())
            .build()
        Logan.init(config)
    }
}