package com.pds.sdk.use

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pds.sdk.use.activity.ActivityLibUse
import com.pds.sdk.use.realm.RealmTestActivity
import org.jetbrains.anko.internals.AnkoInternals


class UseMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun doClick(view: View) {
        AnkoInternals.internalStartActivity(this, RealmTestActivity::class.java, emptyArray())
    }

}