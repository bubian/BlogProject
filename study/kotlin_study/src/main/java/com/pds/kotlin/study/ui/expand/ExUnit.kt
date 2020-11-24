package com.pds.kotlin.study.ui.expand

import android.content.res.Resources

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/25 2:41 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
fun Int.dp() : Float = (this / Resources.getSystem().displayMetrics.density)