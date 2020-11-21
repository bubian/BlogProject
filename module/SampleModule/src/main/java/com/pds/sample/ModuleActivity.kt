package com.pds.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.callback.SimpleItemOnClickListener
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.router.module.SampleGroupRouter
import com.pds.sample.module.html.HtmlCompatActivity
import com.pds.sample.module.html.HtmlTextViewActivity
import com.pds.sample.module.interpolatorplayground.InterpolatorActivity
import kotlinx.android.synthetic.main.recyclerview.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 09:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Route(path = SampleGroupRouter.SAMPLE_HOME)
class ModuleActivity : AppCompatActivity() {

    private val clzArray = arrayOf(
        HtmlCompatActivity::class.java,
        HtmlTextViewActivity::class.java,
        InterpolatorActivity::class.java
    )

    private val titleArray = arrayOf(
        "TextView Compat Html",
        "TextView Html",
        "插值器使用"
    )

    private val contentArray = arrayOf(
        "简介：TextView对Html字符串扩展，支持系统不支持的Html标签",
        "简介：自定义TextView，扩展对Html各种标签支持，功能比系统自带的更加全面和强大",
        "简介：包含各种插值器使用，通过动画形式展示，形象易理解"
    )

    private val contentAdapter =
        object : ListAdapter<SampleEntity>(this@ModuleActivity, R.layout.item_main) {
            override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: SampleEntity?) {
                itemData?.let {
                    baseViewHolder
                        .setText(R.id.title_tv, itemData.title)
                        .setText(R.id.des_tv, itemData.content)
                }
            }
        }.apply {
            setItemChildOnClickListener(object : SimpleItemOnClickListener<SampleEntity>() {
                override fun onItemClick(v: View?, position: Int, data: SampleEntity?) {
                    data?.let {
                        AnkoInternals.internalStartActivity(
                            this@ModuleActivity,
                            data.clz,
                            emptyArray()
                        )
                    }
                }
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)
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
        val data: MutableList<SampleEntity> = ArrayList()
        for ((i, value) in titleArray.withIndex()) {
            data.add(SampleEntity(clzArray[i], value, contentArray[i]))
        }
        contentAdapter.dataList = data
    }
}

