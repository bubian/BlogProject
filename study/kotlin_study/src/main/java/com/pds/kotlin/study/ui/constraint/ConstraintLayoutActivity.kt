package com.pds.kotlin.study.ui.constraint

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.adapter.ContentAdapter
import com.pds.kotlin.study.ui.entity.Entity
import kotlinx.android.synthetic.main.constraint_1.main_content
import kotlinx.android.synthetic.main.constraint_1.toolbar
import kotlinx.android.synthetic.main.constraint_6.*
import kotlinx.android.synthetic.main.constraint_common.*
import java.util.*
import kotlin.math.abs

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-27 11:14
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class ConstraintLayoutActivity : AppCompatActivity() {
    private val contentAdapter = ContentAdapter<Entity>(this)
    private var title = ""

    fun clickFloatButton(view: View) {

    }
    private var layoutId= R.layout.constraint_1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 一定要在setContentView（）之前调用
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        layoutId = intent.getIntExtra("layout_id", R.layout.constraint_1)
        title = intent.getStringExtra("title") ?: "title"
        setContentView(layoutId)
        initView()
        initRecyclerView()
        if (layoutId == R.layout.constraint_6) {
            initBar()
        }
        initData()
    }

    private fun initBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // 添加默认的返回图标
            setDisplayHomeAsUpEnabled(true)
            // 设置返回键可用
            supportActionBar?.setHomeButtonEnabled(true)
        }

        toolbar.menu.apply {
            add("menu")
            addSubMenu("ok")
        }
    }

    private fun showSnackBar() {
        Snackbar
            .make(main_content, "已经滑动底了", Snackbar.LENGTH_SHORT)
            .setAction("确认 ") {}
            .show()
    }

    fun scroll(view: View) {
        recyclerView.smoothScrollBy(0, 200)
    }

    var canScrollVertically = true
    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ConstraintLayoutActivity)
            adapter = contentAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    canScrollVertically = recyclerView.canScrollVertically(1)
                }
            })
        }
    }

    var initY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            initY = ev.y
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (!canScrollVertically and (abs((initY - ev.y)) > 20)) {
                showSnackBar()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun initView() {
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout?.title = title
    }

    private fun initData() {
        val data: MutableList<Entity> = ArrayList()
        while (data.size < 17) {
            data.add(Entity("假数据", null))
        }
        contentAdapter.dataList = data
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, actionMenu.menu)
        return super.onCreateOptionsMenu(menu)
    }
}