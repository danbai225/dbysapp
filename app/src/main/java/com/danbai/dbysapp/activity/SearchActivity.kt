package com.danbai.dbysapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity.CENTER_HORIZONTAL
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.danbai.dbysapp.R
import com.danbai.dbysapp.entity.Ysb
import com.danbai.dbysapp.util.PmUtil
import com.danbai.dbysapp.util.TabUtil
import com.danbai.dbysapp.widget.YsImg
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.flexbox.FlexboxLayout
import com.rengwuxian.materialedittext.MaterialEditText
import com.xwdz.http.QuietOkHttp
import com.xwdz.http.callback.StringCallBack
import okhttp3.Call

class SearchActivity : AppCompatActivity(){
    private var fbl: FlexboxLayout? = null
    private var sou: MaterialEditText?=null
    private var list: List<Ysb>? = null
    private val handler = Handler()
    private var tab:MeowBottomNavigation ?=null
    private val gosou = Runnable {
        Log.d("淡白影视","sou")
        getdate(sou?.text.toString())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ini()
    }
    private fun ini(){
        fbl = findViewById(R.id.searchfbl)
        sou= findViewById(R.id.sou)
        sou!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                handler.removeCallbacks(gosou)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count>0){
                    handler.postDelayed(gosou,2000)
                }
            }
        })
        tab =findViewById(R.id.searchtab)
        tab!!.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
        tab!!.add(MeowBottomNavigation.Model(1, R.drawable.ic_fen))
        tab!!.add(MeowBottomNavigation.Model(2, R.drawable.ic_search))
        tab!!.add(MeowBottomNavigation.Model(3, R.drawable.ic_me))
        tab?.setOnShowListener {
            if(it.id!=2){
                TabUtil.tiao(this,it.id)
            }
        }
    }
    private fun getdate(gjc:String){
        QuietOkHttp.get("https://dbys.vip/api/v1/ys/search/$gjc")
            .execute(object : StringCallBack() {
                override fun onFailure(call: Call, e: Exception) {

                }
                override fun onSuccess(call: Call, response: String) {
                    val jsonObject = JSON.parseObject(response)
                    val tjarry = jsonObject.getJSONArray("data")
                    list= JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    updatefbl()
                }
            })
    }
    @SuppressLint("SetTextI18n")
    private fun updatefbl(){
        if(list!!.size>500){
            list=list!!.subList(0,100)
        }
        fbl?.removeAllViews()
        val textView = TextView(this)
        textView.text = "以下是有关\"${sou?.text}\"的影视"
        textView.gravity = CENTER_HORIZONTAL
        textView.setTextColor(ContextCompat.getColor(this,R.color.db))
        textView.textSize = 24f
        textView.width = PmUtil.getScreenWidth(this)
        fbl?.addView(textView)
        for (ysb in list!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, ysb.zt,ysb.id,PmUtil.getW(this))
            fbl?.addView(dbys)
        }
    }
    override fun onStart() {
        super.onStart()
        tab!!.show(2, true)
    }
}
