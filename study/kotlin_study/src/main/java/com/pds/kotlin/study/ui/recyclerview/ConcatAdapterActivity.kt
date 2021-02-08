package com.pds.kotlin.study.ui.recyclerview

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.pds.base.act.BaseActivity
import com.pds.base.adapter.ListAdapter
import com.pds.base.adapter.viewhold.ViewHolder
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.entity.MainEntity
import com.pds.kotlin.study.ui.recyclerview.helper.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_main.view.*
import kotlinx.android.synthetic.main.my_recycler_view.*
import java.util.*


/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/21 2:01 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class ConcatAdapterActivity : BaseActivity() {

    private lateinit var mConcatAdapter: ConcatAdapter
    private lateinit var adapters : MutableList<RecyclerView.Adapter<RecyclerView.ViewHolder>>
    private lateinit var mItemTouchHelper: ItemTouchHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recycler_view)
        adapters = emptyList<RecyclerView.Adapter<RecyclerView.ViewHolder>>().toMutableList()
        initAdapter()
        mConcatAdapter = ConcatAdapter(adapters)
        // 分割线
        val decoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this,R.drawable.divider_dash_line)?.let {
            decoration.setDrawable(it)
        }
        recyclerView.apply {
            layoutManager = object :LinearLayoutManager(this@ConcatAdapterActivity){
                override fun supportsPredictiveItemAnimations(): Boolean {
                    return true
                }
            }
            // 如果不加 会显示成实线
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adapter = mConcatAdapter
            // itemAnimator = MyChangeAnimator()
            itemAnimator = DefaultItemAnimator()
            // 禁止recyclerViewItem改变动画
            // (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
             addItemDecoration(decoration)
        }
    }

    private fun initAdapter() {
        adapters.add(RLAdapter(this@ConcatAdapterActivity).apply {
            val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(this)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper.attachToRecyclerView(recyclerView)
        })
        adapters.add(RVAdapter(recyclerView).apply {
            val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(this)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper.attachToRecyclerView(recyclerView)
        })
        initData()
    }

    private fun initData() {
        val data: MutableList<MainEntity> = ArrayList()
        for ((i, value) in RLAdapter.titleArray.withIndex()) {
            data.add(MainEntity(clz = RLAdapter.clzArray[i],title = value, content = RLAdapter.contentArray[i],type = RLAdapter.typeArray[i]))
        }
        (adapters[0] as ListAdapter<*>).dataList = data
    }
}