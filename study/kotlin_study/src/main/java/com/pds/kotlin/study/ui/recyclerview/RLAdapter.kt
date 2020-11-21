package com.pds.kotlin.study.ui.recyclerview

import android.content.Context
import android.view.View
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.callback.SimpleItemOnClickListener
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.constraint.ConstraintMainActivity
import com.pds.kotlin.study.ui.entity.MainEntity
import com.pds.kotlin.study.ui.recyclerview.helper.ItemTouchHelperAdapter
import com.pds.kotlin.study.ui.recyclerview.helper.ItemTouchHelperViewHolder
import kotlinx.android.synthetic.main.item_main.view.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/20 7:06 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

open class RLAdapter(context: Context, id: Int = R.layout.item_main) :
    ListAdapter<MainEntity>(context, id) , ItemTouchHelperAdapter {

    init {
        setItemChildOnClickListener(object : SimpleItemOnClickListener<MainEntity>() {
            override fun onItemClick(v: View, position: Int, data: MainEntity?) {
                data?.let {
                    AnkoInternals.internalStartActivity(
                        v.context,
                        data.clz,
                       arrayOf(Pair("type",data.type))
                    )
                }
            }
        })
    }

    companion object {
        val typeArray = arrayOf(1, 2,3,4)

        val clzArray = arrayOf(
            LayoutManagerActivity::class.java,
            LayoutManagerActivity::class.java,
            LayoutManagerActivity::class.java,
            ConstraintMainActivity::class.java
        )

        val titleArray = arrayOf(
            "GridLayoutManager",
            "StaggeredGridLayoutManager",
            "StaggeredGridLayoutManager",
            "FlexboxLayoutManager"
        )
        val contentArray = arrayOf(
            "第一个adapter",
            "水平方向",
            "竖直方向",
            "第一个adapter"
        )
    }

    override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: MainEntity) {
        itemData?.let {
            baseViewHolder.convertView.title_tv.apply {
                text = itemData.title
            }
            baseViewHolder.convertView.des_tv.apply {
                text = itemData.content
            }
        }
    }

    override fun onItemDismiss(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // 首先保证数据集合数据正确
        Collections.swap(dataList, fromPosition, toPosition)
        // 通知item发生移动
        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}