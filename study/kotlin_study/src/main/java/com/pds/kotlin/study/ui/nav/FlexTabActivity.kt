package com.pds.kotlin.study.ui.nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.pds.kotlin.study.R
import com.pds.ui.nav.slidingtab.RoundSlidingTabStrip
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

    private var mStatePagerAdapter: FragmentStatePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flex_tab)
        chat_tab.setTabStrip(RoundSlidingTabStrip(this))
        initFragments()
    }

    private fun initFragments() {
        mStatePagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
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
        chat_vp.offscreenPageLimit = 3
        chat_vp.adapter = mStatePagerAdapter

        InternalViewUtil.initSlidingTablayout(chat_tab, chat_vp, true
            , object : SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    mCurrentTabPosition = position
                }
            })
        chat_tab.setCustomTabView(R.layout.view_title_article_tab, R.id.tv_article_title_id)
        chat_tab.setSelectRelativeTextColorsRes(R.color.white, R.color.c_454553)
        chat_tab.setSelectedIndicatorColors(resources.getColor(R.color.c_007aff))
        chat_tab.setBottomBorderIndicatorRatio(0.9f)
        chat_tab.setViewPager(chat_vp)
        chat_vp.currentItem = 0
    }
}