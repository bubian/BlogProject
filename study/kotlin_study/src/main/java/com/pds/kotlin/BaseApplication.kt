package com.pds.kotlin

import android.util.Log
import androidx.multidex.MultiDexApplication

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 11:18
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class BaseApplication : MultiDexApplication(){

    companion object{
        private const val TAG = "BaseApplication"
        private lateinit var application : BaseApplication
        fun app() : BaseApplication{
            return application
        }
    }
    override fun onCreate() {
        super.onCreate()
        application = this
        Log.d(TAG,"onCreate")
    }
}