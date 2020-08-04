package com.pds.sdk.use

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pds.sdk.use.activity.ActivityLibUse
import org.jetbrains.anko.internals.AnkoInternals


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun doClick(view: View) {
        AnkoInternals.internalStartActivity(this,ActivityLibUse::class.java, emptyArray())
    }

}