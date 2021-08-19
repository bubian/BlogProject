package com.pds.kotlin

import android.app.Application
import com.pds.kotlin.study.ModuleKotlin

class DebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ModuleKotlin.instance().init(this)
    }
}