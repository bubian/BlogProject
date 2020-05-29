package com.pds.kotlin.study.ui.constraint

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.flexbox.*
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.SimpleItemOnClickListener
import com.pds.base.holder.BaseViewHolder
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.entity.Entity
import kotlinx.android.synthetic.main.constraint_common.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.ArrayList

class ConstraintMainActivity : Activity(){

    private val contentAdapter = ContentAdapter(this)
    private val textArray = arrayOf(
        // 下面都是在recyclerView进行滑动操作
        // 向上滑动（不管recyclerView是否已经滚动顶部了），appbar收缩到Toolbar设定的最小高度；向下滑动（当recyclerView滑动到顶部时），appbar开始随着下滑开始拉伸到Toolbar设定的高度，
        // 如果在可见和显示最大高度之间停止滑动，appbar将保持当前的显示状态
        "exitUntilCollapsed",
        // 向上滚动（不管recyclerView是否已经滚动顶部了），appbar随着上滑移动到不可见；向下滑动（不管recyclerView是否已经滚动顶部了），appbar随着下滑移至可见高度（Toolbar设定的高度），
        // 如果在可见和显示最大高度之间停止滑动，appbar将保持当前的显示状态
        "enterAlways",
        // 向上滑动（不管recyclerView是否已经滚动顶部了），appbar收缩到Toolbar设定的最小高度；向下滑动（当recyclerView滑动到顶部时），appbar随着下滑移至可见高度（Toolbar设定的高度），
        // 如果在可见和显示最大高度之间停止滑动，appbar将保持当前的显示状态
        "enterAlwaysCollapsed",
        // 和 enterAlwaysCollapsed差不多，不同的是：如果在可见和显示最大高度之间停止滑动，
        // 会根据当前appbar向上移动或者向下移动距离是否大于appbar最大显示高度的50%来决定是否恢复到开始状态或者更新到新状态（即向上移动到Toolbar的上边界或者向下移动到最大可见状态）；
        // 为什么是Toolbar的上边界，可以给Toolbar设置Margins来查看效果，结合snapMargins更加明显差异。
        "snap",
        // 单独使用，和enterAlways效果一样；
        // 和 snap结合使用，Toolbar 将会被 snap 到它的顶部外边距和它的底部外边距的位置，而不是这个 Toolbar 自身的上下边缘。
        "snapMargins",
        // CollapsingToolbarLayout是用于实现折叠式应用栏的包装器，旨在用作包装的直接子代。
        "CollapsingToolba",
        // 它跟 Guideline  一样属于Virtual Helper objects，在运行时的界面上看不到。Barrier 和 Guideline 的区别在于它是由多个 view 的大小决定的
        "Barrier",
        // constraint基本布局属性
        "constraint",
        // Placeholder使用
        "template",
        "bottomSheetBehavior",
        "CustomBottomSheetBehavior",
        "BottomSheet"
        )
    private val layoutArray = arrayOf(
        R.layout.constraint_1,
        R.layout.constraint_2,
        R.layout.constraint_3,
        R.layout.constraint_4,
        R.layout.constraint_5,
        R.layout.constraint_6,
        R.layout.barrier_1,
        R.layout.constraint_7,
        R.layout.template_impl,
        R.layout.behavior_1,
        R.layout.behavior_custom_activity,
        R.layout.ui_main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.constraint_main)
        initView()
        initData()
    }

    private fun initView() {
        val flm =  FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
            justifyContent = JustifyContent.FLEX_START
        }

        recyclerView.apply {
            layoutManager = flm
            adapter = contentAdapter
        }

    }

    private fun initData() {
        val data: MutableList<Entity> = ArrayList()
        for ((i ,value) in textArray.withIndex()) {
            val entity : Entity = if (i >6){
                Entity(value,layoutArray[i],R.color.colorAccent)
            }else{
                Entity(value,layoutArray[i])
            }
            data.add(entity)
        }
        contentAdapter.apply {
            dataList = data
            setItemChildOnClickListener(object : SimpleItemOnClickListener<Entity>() {
                override fun onItemClick(v: View?, position: Int, data: Entity?) {
                    when (data?.layoutId) {
                        R.layout.barrier_1,R.layout.constraint_7,R.layout.template_impl ->{
                            AnkoInternals.internalStartActivity(this@ConstraintMainActivity,ConstraintActivity::class.java, arrayOf(Pair("layout_id", data?.layoutId),
                                Pair("title",data?.text)
                            ))
                        }
                        R.layout.behavior_custom_activity -> {
                            AnkoInternals.internalStartActivity(this@ConstraintMainActivity, CustomBehaviorActivity::class.java, emptyArray())
                        }
                        R.layout.ui_main -> {
                            AnkoInternals.internalStartActivity(this@ConstraintMainActivity, BottomSheetActivity::class.java, emptyArray())
                        }

                        R.layout.behavior_1 ->{
                            AnkoInternals.internalStartActivity(
                                this@ConstraintMainActivity, BehaviorActivity::class.java, arrayOf(
                                    Pair("layout_id", data?.layoutId),
                                    Pair("title", data?.text)
                                )
                            )
                        }

                        else -> {
                            AnkoInternals.internalStartActivity(this@ConstraintMainActivity,ConstraintLayoutActivity::class.java, arrayOf(Pair("layout_id", data?.layoutId),
                                Pair("title",data?.text)
                            ))
                        }
                    }
                }
            })
        }
    }


    inner class ContentAdapter(context: Context) : ListAdapter<Entity>(context){

        override fun convert(baseViewHolder: BaseViewHolder, position: Int, itemData: Entity?) {
            baseViewHolder.setText(baseViewHolder.convertView,itemData?.text)
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
}