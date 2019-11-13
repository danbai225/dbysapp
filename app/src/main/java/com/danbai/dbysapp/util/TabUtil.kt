package com.danbai.dbysapp.util

import android.content.Context
import android.content.Intent
import com.danbai.dbysapp.activity.FenActivity
import com.danbai.dbysapp.activity.MainActivity
import com.danbai.dbysapp.activity.MeActivity
import com.danbai.dbysapp.activity.SearchActivity

object TabUtil{
    fun tiao(context: Context, id: Int) {
        val intent: Intent
        when (id) {
            0->{
                intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
            1 -> {
                intent = Intent(context, FenActivity::class.java)
                context.startActivity(intent)
            }
            2 -> {
                intent = Intent(context, SearchActivity::class.java)
                context.startActivity(intent)
            }
            3 -> {
                intent = Intent(context, MeActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    fun tiao(context: Context, av:Class<*>) {
        val intent: Intent = Intent(context,av)
        context.startActivity(intent)
    }
}