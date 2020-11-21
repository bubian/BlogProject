package com.pds.kotlin.study.ui.entity

import android.app.Activity
import com.pds.base.act.BaseActivity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 12:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
data class MainEntity(
    var clz: Class<out Activity> = BaseActivity::class.java,
    var title: String = "",
    var content: String = "",
    var type : Int = 0
)