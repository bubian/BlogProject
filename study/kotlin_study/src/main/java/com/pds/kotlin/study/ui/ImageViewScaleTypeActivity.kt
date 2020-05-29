package com.pds.kotlin.study.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-27 09:15
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class ImageViewScaleTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acticity_imageview_scaletype)
        val bitmap = BitmapFactory.decodeResource(resources,
            R.mipmap.kkkk
        )
        Log.e("ScaleType","kkkk:w = ${bitmap.width} h = ${bitmap.height}")
    }

}