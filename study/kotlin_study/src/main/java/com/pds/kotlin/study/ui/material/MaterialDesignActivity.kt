package com.pds.kotlin.study.ui.material

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.pds.base.adapter.SimpleItemOnClickListener
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.adapter.ContentAdapter1
import com.pds.kotlin.study.ui.entity.Entity
import kotlinx.android.synthetic.main.constraint_common.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 11:36
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class MaterialDesignActivity : Activity() {
    private val textArray = arrayOf(
        "Badge+BottomNavigation,TabLayout",
        "BottomAppBar,Button,Checkboxes,Chips",
        "Text fields",
        "DrawerLayout,NavigationView",
        "RadioButton,SwitchMaterial"
    )

    private val layoutIdArray = arrayOf(
        R.layout.material_badge_navigation,
        R.layout.material_bottomappbar,
        R.layout.material_textfields,
        R.layout.material_navigationview,
        R.layout.material_radiobutton
    )

    private val clzArray = arrayOf(
        BottomNavigationActivity::class.java,
        CommonMaterialComponentActivity::class.java,
        CommonMaterialComponentActivity::class.java,
        CommonMaterialComponentActivity::class.java,
        CommonMaterialComponentActivity::class.java
    )

    private val contentAdapter = ContentAdapter1(this@MaterialDesignActivity,R.layout.item_main).apply {
        setItemChildOnClickListener(object : SimpleItemOnClickListener<Entity>(){
            override fun onItemClick(v: View?, position: Int, data: Entity?) {
                data?.let {
                    AnkoInternals.internalStartActivity(this@MaterialDesignActivity,
                        data.clz ?: BottomNavigationActivity::class.java,
                        arrayOf("layout_id" to data.layoutId, "title" to data.text
                    ))
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_design)
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
            data.add(Entity(text = value,layoutId = layoutIdArray[i],clz = clzArray[i]))
        }
        contentAdapter.dataList = data
    }

    private fun showMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.bottom_navigation_menu, popup.menu)
        popup.show()
        return true
    }

    fun clickFloatButton(view: View){
        showMenu(view)
    }
}