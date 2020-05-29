package com.pds.kotlin.study.ui.entity

import android.app.Activity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 12:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
data class MainEntity(var clz : Class<out Activity>, var title : String = "", var content : String = "")