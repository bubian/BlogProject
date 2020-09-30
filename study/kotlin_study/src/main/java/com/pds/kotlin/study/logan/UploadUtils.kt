package com.pds.kotlin.study.logan

import android.annotation.SuppressLint
import com.dianping.logan.Logan
import com.pds.log.core.Lg
import java.text.SimpleDateFormat


/**
 * @author: pengdaosong
 * CreateTime:  2020-06-12 15:46
 * Email：pengdaosong@medlinker.com
 * Description:
 */
const val TAG = "Logan_Upload"
fun uploadLogFile(){
    val url = "https://openlogan.inf.test.sankuai.com/logan/upload.json"
    Logan.s(
        url,
        loganTodayDate(),
        "testAppId",
        "testUnionid",
        "testdDviceId",
        "testBuildVersion",
        "testAppVersion"
    ) { statusCode, data ->
        val resultData = data?.let { String(it) } ?: ""
        Lg.d(TAG, "upload result, httpCode: $statusCode, details: $resultData")
    }
}

@SuppressLint("SimpleDateFormat")
fun loganTodayDate(): String? {
    return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis())
}


