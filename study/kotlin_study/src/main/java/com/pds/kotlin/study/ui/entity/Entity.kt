package com.pds.kotlin.study.ui.entity

import android.app.Activity
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-28 14:18
 * Email：pengdaosong@medlinker.com
 * Description:
 */
data class Entity(
    var text: String = "",
    var layoutId: Int? = null,
    var color: Int = R.color.colorPrimary,
    var clz: Class<out Activity>? = null)