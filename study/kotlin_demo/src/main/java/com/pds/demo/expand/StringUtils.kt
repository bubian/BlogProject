package com.pds.demo.expand

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * @author: pengdaosong
 * @CreateTime:  2020/12/21 9:27 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
fun String.isPhoneNo() : Boolean{
    if (TextUtils.isEmpty(this)) {
        return false
    }
    val p = Pattern.compile("^((1[0-9][0-9])\\d{8}$)")
    val m = p.matcher(this)
    return m.matches()
}
