package com.pds.kotlin.study.ui.recyclerview

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.base.act.BaseActivity
import com.pds.kotlin.study.R
import com.pds.router.module.ModuleGroupRouter
import kotlinx.android.synthetic.main.my_recycler_view.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/20 6:55 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class RLAnimaActivity : BaseActivity() {
    var mDefaultItemAnimator = DefaultItemAnimator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recycler_view)
        recyclerView.layoutManager = MyLinearLayoutManager(this)
        recyclerView.adapter = RLAdapter<RecyclerView.ViewHolder>()
    }

    private class MyLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
        override fun supportsPredictiveItemAnimations(): Boolean {
            return super.supportsPredictiveItemAnimations()
        }
    }
}