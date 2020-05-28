package com.pds.kotlin.study.constraint

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pds.kotlin.study.R
import com.pds.kotlin.study.adapter.ContentAdapter
import kotlinx.android.synthetic.main.behavior_1.*
import kotlin.math.abs

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-28 14:09
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class BehaviorActivity : AppCompatActivity() {

    private val contentAdapter = ContentAdapter<String>(this)

    private val behavior by lazy(mode =  LazyThreadSafetyMode.SYNCHRONIZED ) {
        BottomSheetBehavior.from(recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = intent.getIntExtra("layout_id", R.layout.constraint_1)
        setContentView(layoutId)
        toolbar.title = intent.getStringExtra("title") ?: "title"

        initRecyclerView()
    }

    var canScrollVerticallyUp = true
    var canScrollVerticallyDown = true
    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@BehaviorActivity)
            adapter = contentAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    canScrollVerticallyUp = recyclerView.canScrollVertically(1)
                    canScrollVerticallyDown = recyclerView.canScrollVertically(-1)
                }
            })
        }
        contentAdapter.dataList = ContentAdapter.INIT_DATA
    }

    private var initY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            initY = ev.y
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (!canScrollVerticallyUp and (abs((initY - ev.y)) > 20)) {

            }else if (!canScrollVerticallyDown and (abs((initY - ev.y)) > 20)) {
                contentAdapter.dataList = ContentAdapter.INIT_DATA
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }
        return super.dispatchTouchEvent(ev)
    }

    fun clickFloatButton(view: View) {
        if(behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            contentAdapter.appendData(ContentAdapter.INIT_DATA_MORE)
            behavior.apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isFitToContents = true

            }
        }
    }
}