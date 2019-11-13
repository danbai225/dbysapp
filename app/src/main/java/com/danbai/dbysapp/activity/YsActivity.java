package com.danbai.dbysapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdnbye.sdk.P2pEngine;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.entity.Ji;
import com.danbai.dbysapp.entity.Ysb;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;

import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
public class YsActivity extends AppCompatActivity {

    StandardGSYVideoPlayer videoPlayer;
    OrientationUtils orientationUtils;
    Ysb ys;
    List<Ji> jiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        videoPlayer = findViewById(R.id.detail_player);
        int ysid = intent.getIntExtra("ysid", 1);
        QuietOkHttp.get("http://39.108.110.44:8081/getys")
                .addParams("id", String.valueOf(ysid))
                .execute(new StringCallBack() {
                    @Override
                    public void onFailure(Call call, Exception e) {

                    }
                    @Override
                    protected void onSuccess(Call call, String response) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        ys = jsonObject.getJSONObject("ys").toJavaObject(Ysb.class);
                        if (ys.getGkdz().equals("[]")) {
                            jiList = JSON.parseArray(ys.getXzdz(), Ji.class);
                        } else {
                            jiList = JSON.parseArray(ys.getGkdz(), Ji.class);
                            ArrayList<Ji> jis = new ArrayList<>();
                            for (Ji ji:jiList) {
                                String parsedUrl = P2pEngine.getInstance().parseStreamUrl(ji.getUrl());
                                ji.setUrl(parsedUrl);
                                jis.add(ji);
                            }
                            jiList=jis;
                        }
                        videoPlayer.setUp(jiList.get(0).getUrl(), true, ys.getPm()+jiList.get(0).getName());
                        videoPlayer.startPlayLogic();

                    }
                });
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //全屏动画效果
        videoPlayer.setShowFullAnimation(true);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.startWindowFullscreen(v.getContext(), false, true);
            }
        });
        //全屏琐横屏
        videoPlayer.setLockLand(true);
        //全屏返回监听
        videoPlayer.setBackFromFullScreenListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        Log.e("淡白影视","返回3");
        if(videoPlayer.isIfCurrentIsFullscreen()){
            GSYVideoManager.backFromWindowFull(this);
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
