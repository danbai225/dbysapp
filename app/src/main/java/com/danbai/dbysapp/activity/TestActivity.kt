package com.danbai.dbysapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.danbai.dbysapp.R
import com.danbai.dbysapp.util.TabUtil
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class TestActivity : AppCompatActivity() {
    private var tab:MeowBottomNavigation ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        ini()
    }

    private fun ini() {
        tab = findViewById(R.id.testtab)
        tab?.add(MeowBottomNavigation.Model(0, R.mipmap.home))
        tab?.add(MeowBottomNavigation.Model(1, R.mipmap.fen))
        tab?.add(MeowBottomNavigation.Model(2, R.mipmap.search))
        tab?.add(MeowBottomNavigation.Model(3, R.mipmap.me))
        tab?.setOnShowListener {
            if(it.id!=3){
                TabUtil.tiao(this,it.id)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        tab!!.show(3, true)
    }
}
