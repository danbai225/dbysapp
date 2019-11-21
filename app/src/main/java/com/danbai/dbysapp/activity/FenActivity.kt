package com.danbai.dbysapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.danbai.dbysapp.R
import com.danbai.dbysapp.fragment.FenFragmentPagerAdapter
import com.danbai.dbysapp.util.TabUtil
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.tabs.TabLayout
import com.umeng.analytics.MobclickAgent


class FenActivity : AppCompatActivity() {
    private var mTabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null
    private var ffadp: FenFragmentPagerAdapter? = null
    private var one: TabLayout.Tab? = null
    private var two: TabLayout.Tab? = null
    private var three: TabLayout.Tab? = null
    private var tab: MeowBottomNavigation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fen)
        //初始化视图
        initViews()
    }

    private fun initViews() {
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = findViewById(R.id.fenViewPager)
        ffadp = FenFragmentPagerAdapter(supportFragmentManager)
        mViewPager!!.adapter = ffadp

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = findViewById(R.id.fenTablayout)
        mTabLayout!!.setupWithViewPager(mViewPager)
        one = mTabLayout!!.getTabAt(0)
        two = mTabLayout!!.getTabAt(1)
        three = mTabLayout!!.getTabAt(2)
        //底部导航
        tab = findViewById(R.id.fentab)
        tab!!.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
        tab!!.add(MeowBottomNavigation.Model(1, R.drawable.ic_fen))
        tab!!.add(MeowBottomNavigation.Model(2, R.drawable.ic_search))
        tab!!.add(MeowBottomNavigation.Model(3, R.drawable.ic_me))
        tab?.setOnShowListener {
            if(it.id!=1){
             TabUtil.tiao(this,it.id)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        tab!!.show(1, true)
    }
    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}
