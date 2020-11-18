package com.pds.sample.module.splugin

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/18 11:15 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

/**
 * 使用插件化，不需要在AndroidManifest注册
 */
class SPluginReplaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildTextView())
    }

    private fun buildTextView(): TextView {
        val textView = TextView(this)
        textView.text = "我是真正需要启动的页面"
        textView.textSize = 26F
        textView.setTextColor(Color.BLACK)
        val p = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.gravity = Gravity.CENTER
        textView.layoutParams = p
        return textView
    }
}