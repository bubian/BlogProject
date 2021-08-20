package com.pds.kotlin.study

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.callback.SimpleItemOnClickListener
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.ui.ViewActivity
import com.pds.kotlin.study.ui.constraint.ConstraintMainActivity
import com.pds.kotlin.study.ui.damp.DampActivity
import com.pds.kotlin.study.ui.entity.MainEntity
import com.pds.kotlin.study.ui.gradient.GradientActivity
import com.pds.kotlin.study.ui.material.MaterialDesignActivity
import com.pds.kotlin.study.ui.nav.FlexTabActivity
import com.pds.kotlin.study.ui.recyclerview.ConcatAdapterActivity
import com.pds.router.module.ModuleGroupRouter
import com.pds.kotlin.study.ui.shadow.ShadowActivity
import kotlinx.android.synthetic.main.constraint_common.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*
import com.google.androidstudio.motionlayoutexample.MotionLayoutActivity
import com.pds.sample.ModuleActivity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 09:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Route(path = ModuleGroupRouter.UI_STUDY)
class UiStudyActivity : AppCompatActivity() {

    private val clzArray = arrayOf(
        ConstraintMainActivity::class.java,
        ViewActivity::class.java,
        MaterialDesignActivity::class.java,
        FlexTabActivity::class.java,
        ConcatAdapterActivity::class.java,
        DampActivity::class.java,
        ShadowActivity::class.java,
        GradientActivity::class.java,
        MotionLayoutActivity::class.java,
        ModuleActivity::class.java
    )

    private val titleArray = arrayOf(
        "Constraint",
        "Custom View 基础",
        "Material Design",
        "flex tab",
        "RecyclerView使用",
        "Spring Animation",
        "shadow学习",
        "Gradient（渐变）学习",
        "Google MotionLayout",
        "个人UI使用集合"
    )

    private val contentArray = arrayOf(
        "简介：包含Constraint基本布局使用，CoordinatorLayout与AppBarLayout，CollapsingToolbarLayout结合使用的各种效果展示；bottom sheet使用，包含大量demo展示，不同属性对比演示等",
        "简介：参考HenCoder - 扔物线自定义View系列文章，亲自实践，通过例子学习不同api之间的差异，结合自己的理解，加一注释。",
        "简介：通过demo讲解material库组件使用，文章参考：https://material.io/develop/android/components/cards/，demo参考：https://github.com/material-components/material-components-android",
        "简介：自定义tab组件，添加样式更加灵活，可以实现多种tab切换样式",
        "简介：主要展示RecyclerView基本使用(优秀开源库：https://github.com/cymcsg/UltimateRecyclerView))",
        "简介：主要介绍弹性与阻尼动画的一些用法和例子",
        "简介：给View添加阴影的一些方式，以及自定义阴影方式",
        "简介：通过实例带你走进渐变的世界",
        "简介：MotionLayout各种效果展示",
        "简介：个人UI使用集合,包含很多使用技巧"
    )

    private val contentAdapter =
        object : ListAdapter<MainEntity>(this@UiStudyActivity, R.layout.item_main) {
            override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: MainEntity?) {
                itemData?.let {
                    baseViewHolder
                        .setText(R.id.title_tv, itemData.title)
                        .setText(R.id.des_tv, itemData.content)
                }
            }
        }.apply {
            setItemChildOnClickListener(object : SimpleItemOnClickListener<MainEntity>() {
                override fun onItemClick(v: View?, position: Int, data: MainEntity?) {
                    data?.let {
                        AnkoInternals.internalStartActivity(
                            this@UiStudyActivity,
                            data.clz,
                            arrayOf(Pair("type", data.type))
                        )
                    }
                }
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ui_study)
        initView()
        initData()
    }

    private fun initView() {
        val flm = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = flm
            adapter = contentAdapter
        }
    }

    private fun initData() {
        val data: MutableList<MainEntity> = ArrayList()
        for ((i, value) in titleArray.withIndex()) {
            data.add(MainEntity(clzArray[i], value, contentArray[i]))
        }
        contentAdapter.dataList = data
    }
}

