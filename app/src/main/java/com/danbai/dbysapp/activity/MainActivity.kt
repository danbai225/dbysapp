package com.danbai.dbysapp.activity

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.dalong.carrousellayout.CarrouselLayout
import com.danbai.dbysapp.R
import com.danbai.dbysapp.entity.Ysb
import com.danbai.dbysapp.util.PmUtil
import com.danbai.dbysapp.util.TabUtil
import com.danbai.dbysapp.widget.YsImg
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.flexbox.FlexboxLayout
import com.xwdz.http.QuietOkHttp
import com.xwdz.http.callback.StringCallBack
import ezy.boost.update.UpdateManager
import okhttp3.Call
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView

class MainActivity : AppCompatActivity() {
    private var tj: List<Ysb>? = null
    private var dy: List<Ysb>? = null
    private var dsj: List<Ysb>? = null
    private var zy: List<Ysb>? = null
    private var dm: List<Ysb>? = null
    private var gg: String? = null
    private var screenWidth: Int = 0
    private var tab: MeowBottomNavigation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UpdateManager.setWifiOnly(false)
        UpdateManager.setUrl("http://39.108.110.44:8081/api/v1/update", "app")
        UpdateManager.install(this)
        UpdateManager.check(this)
        ini()
    }

    override fun onStart() {
        super.onStart()
        tab!!.show(0, true)
    }

    private fun ini() {
        screenWidth = PmUtil.getScreenWidth(this)
        //获取数据
        QuietOkHttp.get("http://39.108.110.44:8081/sy")
            .execute(object : StringCallBack() {
                override fun onFailure(call: Call, e: Exception) {
                }

                override fun onSuccess(call: Call, response: String) {
                    val jsonObject = JSON.parseObject(response)
                    var tjarry = jsonObject.getJSONArray("tj")
                    tj = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    gg = jsonObject.getString("gg")
                    tjarry = jsonObject.getJSONArray("dy")
                    dy = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    tjarry = jsonObject.getJSONArray("dsj")
                    dsj = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    tjarry = jsonObject.getJSONArray("zy")
                    zy = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    tjarry = jsonObject.getJSONArray("dm")
                    dm = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb::class.java)
                    data()
                }
            })
        //底部导航
        tab = findViewById(R.id.maintab)
        tab!!.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
        tab!!.add(MeowBottomNavigation.Model(1, R.drawable.ic_fen))
        tab!!.add(MeowBottomNavigation.Model(2, R.drawable.ic_search))
        tab!!.add(MeowBottomNavigation.Model(3, R.drawable.ic_me))
        tab?.setOnShowListener {
            if (it.id != 0) {
                TabUtil.tiao(this, it.id)
            }
        }
    }

    private fun data() {
        val w = PmUtil.getW(this)
        //公告
        val htmlTextView = this.findViewById(R.id.gg) as HtmlTextView
        htmlTextView.setHtml(
            gg!!,
            HtmlHttpImageGetter(htmlTextView)
        )
        //轮播图
        val carrouse = findViewById<CarrouselLayout>(R.id.carrousel)
        carrouse.setAutoRotation(true)
        for (ysb in tj!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, null, ysb.id, w)
            dbys.text.setBackgroundColor(ContextCompat.getColor(this, R.color.db))
            carrouse.addView(dbys)
        }
        carrouse.checkChildView()
        carrouse.refreshLayout()
        carrouse.startAnimationR((screenWidth / 2).toFloat(), (screenWidth / 2).toFloat())
        carrouse.setR((screenWidth / 2).toFloat())
        val fbl = findViewById<FlexboxLayout>(R.id.mainbj)
        //最新电影
        fbl.addView(getNewTile(R.string.newdy))
        for (ysb in dy!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, ysb.zt, ysb.id, w)
            fbl.addView(dbys)
        }
        //最新电视剧
        fbl.addView(getNewTile(R.string.newdsj))
        for (ysb in dsj!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, ysb.zt, ysb.id, w)
            fbl.addView(dbys)
        }
        //最新综艺
        fbl.addView(getNewTile(R.string.newzy))
        for (ysb in zy!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, ysb.zt, ysb.id, w)
            fbl.addView(dbys)
        }
        //最新dm
        fbl.addView(getNewTile(R.string.newdm))
        for (ysb in dm!!) {
            val dbys = YsImg(this, ysb.tp, ysb.pm, ysb.zt, ysb.id, w)
            fbl.addView(dbys)
        }

    }

    private fun getNewTile(tile: Int): TextView {
        val textView = TextView(this)
        textView.setText(tile)
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(ContextCompat.getColor(this, R.color.db))
        textView.textSize = 24f
        textView.width = screenWidth
        return textView
    }
}
