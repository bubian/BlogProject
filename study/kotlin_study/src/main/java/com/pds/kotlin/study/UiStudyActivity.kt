package com.pds.kotlin.study

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.callback.SimpleItemOnClickListener
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.recorder.RecordActivity
import com.pds.kotlin.study.ui.ViewActivity
import com.pds.kotlin.study.ui.constraint.ConstraintMainActivity
import com.pds.kotlin.study.ui.entity.MainEntity
import com.pds.kotlin.study.ui.material.MaterialDesignActivity
import com.pds.kotlin.study.ui.nav.FlexTabActivity
import com.pds.kotlin.study.ui.recyclerview.RLAnimaActivity
import com.pds.router.module.ModuleGroupRouter
import kotlinx.android.synthetic.main.constraint_common.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*

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
        RLAnimaActivity::class.java
    )

    private val titleArray = arrayOf(
        "Constraint",
        "Custom View 基础",
        "Material Design",
        "flex tab",
        "RecyclerView使用"
    )

    private val contentArray = arrayOf(
        "简介：包含Constraint基本布局使用，CoordinatorLayout与AppBarLayout，CollapsingToolbarLayout结合使用的各种效果展示；bottom sheet使用，包含大量demo展示，不同属性对比演示等",
        "简介：参考HenCoder - 扔物线自定义View系列文章，亲自实践，通过例子学习不同api之间的差异，结合自己的理解，加一注释。",
        "简介：通过demo讲解material库组件使用，文章参考：https://material.io/develop/android/components/cards/，demo参考：https://github.com/material-components/material-components-android",
        "简介：自定义tab组件，添加样式更加灵活，可以实现多种tab切换样式",
        "简介：主要展示RecyclerView基本使用，包括动画等"
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
                            emptyArray()
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

