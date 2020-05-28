package com.pds.kotlin.study.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.pds.base.adapter.ListAdapter
import com.pds.base.holder.BaseViewHolder
import com.pds.kotlin.study.entity.Entity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-28 14:31
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class ContentAdapter<T>(context: Context) : ListAdapter<T>(context){

    companion object{
        val INIT_DATA  = mutableListOf(
            "数据：1",
            "数据：2"
        )
        val INIT_DATA_MORE= mutableListOf(
            "数据：3",
            "数据：4",
            "数据：5",
            "数据：6",
            "数据：7",
            "数据：8",
            "数据：9",
            "数据：10",
            "数据：11",
            "数据：12",
            "数据：13",
            "数据：14",
            "数据：15",
            "数据：16",
            "数据：17")
    }


    override fun convert(baseViewHolder: BaseViewHolder, position: Int, itemData: T?) {
        if (itemData is Entity){
            baseViewHolder.setText(baseViewHolder.convertView,itemData?.text)
        }else if (itemData is String){
            baseViewHolder.setText(baseViewHolder.convertView,itemData)
        }
    }
    override fun createItemView(parent: ViewGroup?, viewType: Int): View {
        return createTextView(context)
    }

    private fun createTextView(context: Context) : TextView {

        val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
        return TextView(context).apply {
            layoutParams = params
            textSize = 16f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
    }
}