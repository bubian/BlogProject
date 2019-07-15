package com.pds.blog

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import blog.pds.com.three.gson.GsonHelper
import blog.pds.com.three.gson.entity.GsonItem

class MainActivityK : AppCompatActivity() {

    companion object {
        const val TAG = "HtmlCompatActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()

        System.loadLibrary("")
    }

    private fun test(){
        val data = "{" +
                "\"user_phone\": \"\"," +
                "\"errmsg\": \"success\"" +
                "}"
        val item = GsonHelper.fromJson(data, GsonItem::class.java)

        Log.d(TAG,"---->fromJson:${item.toString()}")
    }
}
