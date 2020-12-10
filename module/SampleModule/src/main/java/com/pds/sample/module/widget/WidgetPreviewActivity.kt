package com.pds.sample.module.widget

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.base.act.BaseCompatActivity
import com.pds.router.module.BundleKey
import com.pds.router.module.SampleGroupRouter
import com.pds.sample.R
import com.pds.ui.jsonviewer.JsonRecyclerView

/**
 * @author: pengdaosong
 * @CreateTime:  2020/12/10 11:13 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.WIDGET_PREVIEW)
class WidgetPreviewActivity : BaseCompatActivity() {
    companion object {
        const val EXPAND = "展开"
        const val COLLAPSE = "收起"
    }
    private var mIconId = "收起"
    private var mView : View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = buildView(intent.getStringExtra(BundleKey.PARAM))
        setContentView(mView)
    }

    private fun buildView(type: String?): View {
        return when (type) {
            "json" -> {
                mNavigationBar.setTitle("JSON预览")
                mIconId = COLLAPSE
                mNavigationBar.setRightText(mIconId)
                mNavigationBar.setRightButtonOnClickListener {
                    if (mIconId == COLLAPSE) {
                        mIconId = EXPAND
                        if (mView is JsonRecyclerView){
                            (mView as JsonRecyclerView).collapse()
                        }
                    } else {
                        mIconId = COLLAPSE
                        if (mView is JsonRecyclerView){
                            (mView as JsonRecyclerView).expand()
                        }
                    }
                    mNavigationBar.setRightText(mIconId)
                }
                JsonRecyclerView(this).apply {
                    setTextSize(16F)
                    setPadding(16, 16, 16, 16)
                    setScaleEnable(true)
                    bindJson(JSON_PREVIEW)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
            else -> {
                View(this)
            }
        }
    }
}
