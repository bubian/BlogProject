package com.pds.kotlin.study.ui.nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.pds.kotlin.study.R
import com.pds.ui.nav.slidingtab.RoundSlidingTabStrip
import com.pds.ui.nav.slidingtab.SlidingTabLayout
import kotlinx.android.synthetic.main.activity_flex_tab.*

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/3 7:42 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class FlexTabActivity : AppCompatActivity() {

    private var mCurrentTabPosition: Int = 0
    private val mTabTitles = intArrayOf(
        R.string.flex_one,
        R.string.flex_two, R.string.flex_three
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flex_tab)
        chat_tab.setTabStrip(RoundSlidingTabStrip(this))
        initFragments(chat_tab,chat_vp)
        initFragments(chat_one,chat_vp_one)
        initFragments(chat_two,chat_vp_two)
        chat_tab.setSelectRelativeTextColorsRes(R.color.white, R.color.c_454553)
        chat_one.setSelectRelativeTextColorsRes(R.color.colorAccent, R.color.c_454553)
        chat_two.setSelectRelativeTextColorsRes(R.color.colorPrimaryDark, R.color.c_454553)
        chat_two.setBottomBorderIndicatorRatio(0.4F)
        chat_vp.currentItem = 0
    }

    private fun initFragments(slidingTabLayout: SlidingTabLayout,viewPager: ViewPager) {
        val mStatePagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val frag = NavTabFragment()
                val bundle = Bundle()
                bundle.putString("content", "页面：$position")
                frag.arguments = bundle
                return frag
            }

            override fun getCount(): Int {
                return mTabTitles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return getString(mTabTitles[position])
            }
        }

        // chat_vp.setScrollble(false);
        // 不要小于2，移除rn fragment会清除相关数据，这时候执行rn fragment生命周期会崩溃，正在寻找解决方案。
        // mViewPager.setScrollble(false);
        // 不要小于2，移除rn fragment会清除相关数据，这时候执行rn fragment生命周期会崩溃，正在寻找解决方案。
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = mStatePagerAdapter

        InternalViewUtil.initSlidingTablayout(slidingTabLayout, viewPager, true
            , object : SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    mCurrentTabPosition = position
                }
            })
        slidingTabLayout.setCustomTabView(R.layout.view_title_article_tab, R.id.tv_article_title_id)
        slidingTabLayout.setSelectedIndicatorColors(resources.getColor(R.color.c_007aff))
        slidingTabLayout.setBottomBorderIndicatorRatio(0.9f)
        slidingTabLayout.setViewPager(viewPager)
    }
}