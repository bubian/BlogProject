package com.pds.kotlin.study.dokit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * @CreateTime:  2020/9/28 1:17 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class DoKitMockActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dokit)
    }

    fun getCode(view: View) {
        DoKitMock.getLoginSmsCode("15708468804","login")
    }
}