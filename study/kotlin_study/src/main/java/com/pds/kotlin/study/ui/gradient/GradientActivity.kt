package com.pds.kotlin.study.ui.gradient

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.activity_gradient.*

/**
 * @author: pengdaosong
 * @CreateTime:  2021/1/16 11:56 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class GradientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gradient)
        gradientOne()
    }

    companion object {
        @ColorInt
        val colors = intArrayOf(0xFF007AFF.toInt(), 0xFF29BFFF.toInt(), 0xFFF6F7F8.toInt())
    }

    private fun gradientOne() {
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        // gradientDrawable.setGradientCenter(0.33f, 0.33f)
        gradientDrawable.useLevel = true
        gradientDrawable.level = 4000
        gradient.background = gradientDrawable
    }

}