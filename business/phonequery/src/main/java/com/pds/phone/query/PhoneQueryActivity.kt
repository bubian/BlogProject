package com.pds.phone.query

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

open class PhoneQueryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
        }catch (e : Exception){
            e.printStackTrace()
        }
        val v : View? = findViewById(R.id.img)
        v!!.setOnClickListener {
            startActivity(Intent(mThat, SecondActivity::class.java))
            startService(Intent(mThat, OneService::class.java))
        }
    }
}