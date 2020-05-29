package com.pds.kotlin.study.ui.constraint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-28 16:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */

/**
 *
 * Constraint一定要给View添加明确的约束，不然可能会出现布局异常
 *
 * Constraint指定了约束，margin等参数才起作用
 */
class ConstraintActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = intent.getIntExtra("layout_id", R.layout.barrier_1)
        title = intent.getStringExtra("title") ?: "title"
        setContentView(layoutId)
    }
}