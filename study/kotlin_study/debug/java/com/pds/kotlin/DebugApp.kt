package com.pds.kotlin

import android.app.Application
import com.pds.kotlin.study.ModuleKotlin
import com.pds.sample.application.ModuleSample

class DebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ModuleKotlin.instance().init(this)
        ModuleSample.instance().onCreate(this)
    }
}