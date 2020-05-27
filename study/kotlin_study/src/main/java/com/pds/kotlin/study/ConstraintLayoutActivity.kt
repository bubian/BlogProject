package com.pds.kotlin.study

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pds.base.adapter.ListAdapter
import com.pds.base.holder.BaseViewHolder
import kotlinx.android.synthetic.main.activity_constraintlayout.*
import java.util.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-27 11:14
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class ConstraintLayoutActivity : AppCompatActivity() {

    private val contentAdapter = ContentAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraintlayout)
        initView()
        initData()
    }

    private fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contentAdapter
    }

    private fun initData() {
        val data: MutableList<String> =
            ArrayList()
        for (i in 0..17) {
            data.add("数据：$i")
        }
        contentAdapter.dataList = data
    }


    inner class ContentAdapter(context: Context) : ListAdapter<String>(context){

        override fun convert(baseViewHolder: BaseViewHolder, position: Int, itemData: String?) {
           baseViewHolder.setText(baseViewHolder.convertView,itemData)
        }
        override fun createItemView(parent: ViewGroup?, viewType: Int): View {
            return createTextView(context)
        }

        private fun createTextView(context: Context) : TextView{
            val tv = TextView(context)
            val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
            tv.layoutParams = params
            tv.textSize = 16f
            tv.setTextColor(Color.BLACK)
            tv.gravity = Gravity.CENTER
            return tv
        }
    }
}