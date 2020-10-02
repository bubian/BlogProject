package com.pds.kotlin.study.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayoutManager
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.entity.Entity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 12:53
 * Email：pengdaosong@medlinker.com
 * Description:
 */

open class ContentAdapter1 @JvmOverloads constructor(context: Context,layoutId : Int = 0) : ListAdapter<Entity>(context){

    override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: Entity?) {
        if (baseViewHolder.convertView is TextView) (baseViewHolder.convertView as TextView).text = itemData?.text
        itemData?.color?.let {
            if (it == R.color.colorAccent)
                baseViewHolder.convertView.  setBackgroundResource(R.drawable.bg_rect_d81b60_20)
            else  baseViewHolder.convertView.  setBackgroundResource(R.drawable.bg_rect_008577_20)
        }
    }
    override fun createItemView(parent: ViewGroup?, viewType: Int): View {
        return createTextView(context)
    }

    private fun createTextView(context: Context) : View {
        val params: ViewGroup.MarginLayoutParams = FlexboxLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = 50
            leftMargin = 50
        }
        return Button(context).apply {
            layoutParams = params
            textSize = 16f
            setTextColor(Color.WHITE)
            isAllCaps = false
            setPadding(30,0,30,0)
            gravity = Gravity.CENTER

        }
    }
}