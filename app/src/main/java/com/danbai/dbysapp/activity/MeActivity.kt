package com.danbai.dbysapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.danbai.dbysapp.R
import com.danbai.dbysapp.util.TabUtil
import com.etebarian.meowbottomnavigation.MeowBottomNavigation


class MeActivity : AppCompatActivity() {
    private var tab: MeowBottomNavigation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)
        ini()
    }

    private fun ini() {
        tab = findViewById(R.id.metab)
        tab?.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
        tab?.add(MeowBottomNavigation.Model(1, R.drawable.ic_fen))
        tab?.add(MeowBottomNavigation.Model(2, R.drawable.ic_search))
        tab?.add(MeowBottomNavigation.Model(3, R.drawable.ic_me))
        tab?.setOnShowListener {
            if (it.id != 3) {
                TabUtil.tiao(this, it.id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tab!!.show(3, true)
    }
}
