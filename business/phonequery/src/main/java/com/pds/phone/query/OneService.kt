package com.pds.phone.query

import android.util.Log

class OneService : BaseService() {
    var i = 0
    private val TAG = "pds"
    override fun onCreate() {
        super.onCreate()
        object : Thread() {
            override fun run() {
                while (true) {
                    Log.i(TAG, "run: " + i++)
                    try {
                        sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
}