package com.pds.kotlin

import androidx.multidex.MultiDexApplication
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import com.pds.kotlin.study.dagger.ApplicationComponent
import com.pds.kotlin.study.dagger.DaggerApplicationComponent
import com.pds.log.core.Lg
import com.pds.tools.DoKitManager
import xcrash.XCrash
import java.io.File


/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 11:18
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class BaseApplication : MultiDexApplication(){

    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

    companion object{
        private const val TAG = "BaseApplication"
        private lateinit var application : BaseApplication
        @JvmStatic
        fun app() : BaseApplication{
            return application
        }
    }
    override fun onCreate() {
        super.onCreate()
        application = this
        Lg.d(TAG,"onCreate")
        DoKitManager.init(this,"34f2baf949b36b874a2e79e3089ff384")
        // xCrash 能为安卓 app 提供捕获 java 崩溃，native 崩溃和 ANR 的能力。不需要 root 权限或任何系统权限。（https://github.com/iqiyi/xCrash）
        // 日志保存目录：/data/data/PACKAGE_NAME/files/tombstones
        XCrash.init(this)
        // logan:美团日志上报库
        initLogan()
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