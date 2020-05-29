package com.pds.kotlin.study.ui.material

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.material_badge_navigation.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 12:48
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class BottomNavigationActivity : Activity() {

    private var title = ""
    private var layoutId= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutId = intent.getIntExtra("layout_id", R.layout.material_badge_navigation)
        title = intent.getStringExtra("title") ?: "title"
        setContentView(layoutId)
        initNavigation()
        initNavigationOne()
        initBadge()
    }

    private fun initNavigationOne(){
        // 禁止选择后对图片的着色
        bottom_navigation_one.itemIconTintList = null

        bottom_navigation_one.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    // 替换选中后的图片
                    item.setIcon(android.R.drawable.ic_media_play)
                    true
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
                    item.setIcon(android.R.drawable.ic_media_pause)
                    true
                }
                R.id.page_3 -> {
                    // Respond to navigation item 2 click
                    item.setIcon(android.R.drawable.ic_media_rew)
                    true
                }
                R.id.page_4 -> {
                    // Respond to navigation item 2 click
                    item.setIcon(android.R.drawable.ic_media_previous
                    )
                    true
                }
                else -> false
            }
        }
    }


    private fun initNavigation(){
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1,R.id.page_2, R.id.page_3,R.id.page_4 -> true
                else -> false
            }
        }
        val badge = bottom_navigation.getOrCreateBadge(R.id.page_1)
        badge.number = 99
    }

    // In API 18+ (APIs supported by ViewOverlay)
    @SuppressLint("RestrictedApi")
    fun initBadge(){
        // 创建 BadgeDrawable
        val ba = BadgeDrawable.create(this)

        ba.backgroundColor = Color.RED
        ba.number = 121
        ba.maxCharacterCount = 5
        ba.badgeTextColor = Color.WHITE
        ba.isVisible = true
        ba.badgeGravity = BadgeDrawable.TOP_END
        // 不加这个显示不出来
        badge_test_parent.foreground = ba
        // 不这样写，红点显示在左上角了
        badge_test_parent.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            BadgeUtils.attachBadgeDrawable(ba, badge_test, badge_test_parent)
        }


    }

    fun operateBottomNavigation(bottomNavigation: BottomNavigationView,menuItemId : Int){
        bottomNavigation.getBadge(menuItemId)?.let {
            it.isVisible = false
            it.clearNumber()
        }
        // 移除badge
        bottomNavigation.removeBadge(menuItemId)
    }

}