package com.danbai.dbysapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdnbye.sdk.P2pEngine;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.entity.Ji;
import com.danbai.dbysapp.entity.Ysb;
import com.danbai.dbysapp.util.PmUtil;
import com.danbai.dbysapp.widget.DbysPlayer;
import com.google.android.flexbox.FlexboxLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Call;
public class YsActivity extends AppCompatActivity {

    DbysPlayer videoPlayer;
    OrientationUtils orientationUtils;
    Ysb ys;
    protected List<GSYVideoModel> jiList = new ArrayList<>();
    int screenWidth;
    FlexboxLayout ysjis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys);
        init();
    }

    private void init() {
        videoPlayer=findViewById(R.id.player);
        screenWidth = PmUtil.getScreenWidth(this);
        Intent intent = getIntent();
        ysjis = findViewById(R.id.ysjis);
        int ysid = intent.getIntExtra("ysid", 1);
        QuietOkHttp.get("http://39.108.110.44:8081/getys")
                .addParams("id", String.valueOf(ysid))
                .execute(new StringCallBack() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                    }
                    @Override
                    protected void onSuccess(Call call, String response) {
                        //将数据转为json对象
                        JSONObject jsonObject = JSON.parseObject(response);
                        ys = jsonObject.getJSONObject("ys").toJavaObject(Ysb.class);
                        List<Ji> jis;
                        //获取不为空的地址
                        if (ys.getGkdz().equals("[]")) {
                          jis = JSON.parseArray(ys.getXzdz(), Ji.class);
                        } else {
                             jis = JSON.parseArray(ys.getGkdz(), Ji.class);
                        }
                        //将地址转为播放器集对象并添加集按钮
                        for (Ji ji:jis){
                            if(jis.size()>1){
                                ysjis.addView(getJiBt(ji.getName()));
                            }
                            jiList.add(new GSYVideoModel(ji.getUrl(),ji.getName()));
                        }
                        //将未加速的地址赋值保存
                        videoPlayer.nojiasulist=jiList;
                        //对第一个地址开启p2p加速
                        GSYVideoModel ji1 = jiList.get(0);
                        ji1.setUrl(P2pEngine.getInstance().parseStreamUrl(ji1.getUrl()));
                        jiList.set(0,ji1);
                        //播放第一个地址
                        if(jis.size()>1){
                            FancyButton isbt = (FancyButton) ysjis.getFlexItemAt(0);
                            isbt.setBackgroundColor(getResources().getColor(R.color.Red1));
                        }
                        videoPlayer.setUp(jiList, false,0);
                        //添加影视信息
                        TextView pm = findViewById(R.id.ysxx_pm);
                        TextView lx = findViewById(R.id.ysxx_lx);
                        TextView zt = findViewById(R.id.ysxx_zt);
                        TextView dq = findViewById(R.id.ysxx_dq);
                        TextView zy = findViewById(R.id.ysxx_zy);
                        TextView dy = findViewById(R.id.ysxx_dy);
                        TextView gxtime = findViewById(R.id.ysxx_gxtime);
                        TextView sytime = findViewById(R.id.ysxx_sytime);
                        TextView js = findViewById(R.id.ysxx_js);
                        pm.setText("片名:"+ys.getPm());
                        lx.setText("类型:"+ys.getLx());
                        zt.setText("状态:"+ys.getZt());
                        dq.setText("地区:"+ys.getDq());
                        zy.setText("主演:"+ys.getZy());
                        dy.setText("导演:"+ys.getDy());
                        gxtime.setText("更新时间:"+ys.getGxtime());
                        sytime.setText("上映时间:"+ys.getSytime());
                        js.setText("介绍:"+ys.getJs());
                    }
                });
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //全屏动画效果
        videoPlayer.setShowFullAnimation(true);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(v -> videoPlayer.startWindowFullscreen(v.getContext(), false, true));
        //全屏琐横屏
        videoPlayer.setLockLand(true);
        //全屏返回监听
        videoPlayer.setBackFromFullScreenListener(v -> onBackPressed());
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());
        //是否自动旋转
        videoPlayer.setRotateViewAuto(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        if(videoPlayer.isIfCurrentIsFullscreen()){
            GSYVideoManager.backFromWindowFull(this);
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
    private FancyButton getJiBt(String string){
        FancyButton fancyButton = new FancyButton(this);
        fancyButton.setTextColor(getResources().getColor(R.color.db));
        fancyButton.setBackgroundColor(getResources().getColor(R.color.Orange1));
        fancyButton.setFocusBackgroundColor(getResources().getColor(R.color.Green1));
        fancyButton.setRadius(15);
        fancyButton.setIconPadding(10,5,10,5);
        fancyButton.setText(string);
        fancyButton.setOnClickListener(v -> {
            FancyButton lastfb = (FancyButton) ysjis.getFlexItemAt(videoPlayer.getPlayindex());
            lastfb.setBackgroundColor(getResources().getColor(R.color.Orange1));
            FancyButton s=(FancyButton)v;
            for (GSYVideoModel j:jiList) {
                if(j.getTitle().equals(s.getText())){
                    videoPlayer.playindiex(jiList.indexOf(j));
                }
            }
            v.setBackgroundColor(getResources().getColor(R.color.Red1));
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fancyButton.getLayoutParams());
        lp.width=(screenWidth/5)-10;
        lp.setMargins(5, 10, 5, 10);
        fancyButton.setLayoutParams(lp);
        return fancyButton;
    }
}
