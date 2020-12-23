package com.pds.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pds.demo.expand.isPhoneNo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        "1570846008".isPhoneNo()
    }
}