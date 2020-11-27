package com.pds.kotlin.study.ui.shadow

import android.os.Bundle
import androidx.core.view.ViewCompat
import com.pds.base.act.BaseActivity
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.activity_shadow.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/27 1:24 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 参考：https://www.shangmayuan.com/a/ca1d91b5c9904e8d87169295.html
 */
class ShadowActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow)
        ViewCompat.setElevation(shadow_one,20F)
    }
}