package com.pds.netty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.send_heart.setOnClickListener(sendHeartBeatL)
    }

    private val sendHeartBeatL = View.OnClickListener {
        Toast.makeText(
            applicationContext, "按钮按下",
            Toast.LENGTH_SHORT
        ).show()

        try {
            sendHeartBeat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun sendHeartBeat() {
        MotoAppClient().connect("129.0.5.11", 5555)
    }
}
