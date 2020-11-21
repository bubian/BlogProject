package com.pds.kotlin.study.ui.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pds.base.act.BaseActivity
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.R
import com.pds.util.unit.UnitConversionUtils
import kotlinx.android.synthetic.main.my_recycler_view.*
import java.util.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/21 3:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class LayoutManagerActivity : BaseActivity() {
    companion object {
        val titleArray = arrayOf(
            "item 1",
            "item 2",
            "item 3",
            "item 4",
            "item 5",
            "item 6",
            "item 7",
            "item 8",
            "item 9",
            "item 10",
            "item 11",
            "item 12",
            "item 13",
            "item 14",
            "item 15",
            "item 16",
            "item 17",
            "item 18",
            "item 19",
            "item 20",
            "item 21",
            "item 22",
            "item 23",
            "item 24",
            "item 25",
            "item 26",
            "item 27",
            "item 28",
            "item 29",
            "item 30",
            "item 31",
            "item 32",
            "item 33",
            "item 34",
            "item 35",
            "item 36",
            "item 37",
            "item 37",
            "item 39",
            "item 40"
        )
    }

    var random: Random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recycler_view)
        init()
        recyclerView.adapter = object :ListAdapter<String>(this,android.R.layout.test_list_item){
            override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: String?) {
                itemData?.apply {
                    val tx = this
                    (baseViewHolder.convertView as TextView).apply {
                        text = tx
                        gravity = Gravity.CENTER
                        textSize = 20F
                        setTextColor(Color.WHITE)
                        height = UnitConversionUtils.dip2px(this@LayoutManagerActivity,100F)
                        setBackgroundColor(generateColor())
                    }
                }
                when (intent.getIntExtra("type", 0)) {
                    2,3 ->{
                        (baseViewHolder.convertView as TextView).apply {
                            width = UnitConversionUtils.dip2px(this@LayoutManagerActivity,130F)
                            height = random.nextInt(200) + UnitConversionUtils.dip2px(this@LayoutManagerActivity,40F)
                        }
                    }
                }
            }
        }.apply {
            dataList = titleArray.toMutableList()
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun init() = when (intent.getIntExtra("type", 0)) {
        1 -> {
            recyclerView.layoutManager = GridLayoutManager(this@LayoutManagerActivity,3).apply {
                this.reverseLayout = false
            }
            val dp8  = UnitConversionUtils.dip2px(this,8F)
            recyclerView.addItemDecoration(GridSpaceItemDecoration(3,dp8,dp8))
        }
        2 ->{
            // orientation，StaggeredGridLayoutManager.VERTICAL代表有多少列；StaggeredGridLayoutManager.HORIZONTAL就代表有多少行
            recyclerView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
            recyclerView.addItemDecoration(DividerGridItemDecoration(this))
        }
        3 ->{
            recyclerView.addItemDecoration(DividerGridItemDecoration(this))
            recyclerView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        }
        else -> {
            recyclerView.layoutManager = LinearLayoutManager(this@LayoutManagerActivity)
        }
    }
    private fun generateColor(): Int {
        val red = (Math.random() * 200).toInt()
        val green = (Math.random() * 200).toInt()
        val blue = (Math.random() * 200).toInt()
        return Color.rgb(red, green, blue)
    }
}