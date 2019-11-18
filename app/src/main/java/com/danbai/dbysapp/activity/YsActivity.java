package com.danbai.dbysapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.entity.Ji;
import com.danbai.dbysapp.entity.Ysb;
import com.danbai.dbysapp.util.PmUtil;
import com.danbai.dbysapp.widget.DanmakuVideoPlayer;
import com.google.android.flexbox.FlexboxLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Call;

public class YsActivity extends AppCompatActivity {
    private boolean isPlay;
    private boolean isPause;
    private boolean isDestory;
    private OrientationUtils orientationUtils;

    int playindex = 0;
    DanmakuVideoPlayer  videoPlayer;
    Ysb ys;
    protected List<Ji> jiList = new ArrayList<>();
    int screenWidth;
    FlexboxLayout ysjis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys);
        videoPlayer = findViewById(R.id.player);
        init();
    }

    private void init() {
        //屏幕宽
        screenWidth = PmUtil.getScreenWidth(this);
        //初始化全屏切换图标
        videoPlayer.setShrinkImageRes(R.drawable.custom_shrink);
        videoPlayer.setEnlargeImageRes(R.drawable.custom_enlarge);

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.tip);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);


        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        videoPlayer.setIsTouchWiget(true);
        //关闭自动旋转
        videoPlayer.setRotateViewAuto(false);
        videoPlayer.setLockLand(false);
        videoPlayer.setShowFullAnimation(false);
        videoPlayer.setNeedLockFull(true);

        //detailPlayer.setOpenPreView(true);
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                videoPlayer.startWindowFullscreen(YsActivity.this, true, true);
            }
        });

        videoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
              //  getDanmu();
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
            }
        });

        videoPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });
        //数据加载
        Intent intent = getIntent();
        ysjis = findViewById(R.id.ysjis);
        int ysid = intent.getIntExtra("ysid", 1);
        QuietOkHttp.get("http://39.108.110.44:8081/getys")
                .addParams("id", String.valueOf(ysid))
                .execute(new StringCallBack() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                    }
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onSuccess(Call call, String response) {
                        //将数据转为json对象
                        JSONObject jsonObject = JSON.parseObject(response);
                        ys = jsonObject.getJSONObject("ys").toJavaObject(Ysb.class);
                        //获取不为空的地址
                        if (ys.getGkdz().equals("[]")) {
                            jiList = JSON.parseArray(ys.getXzdz(), Ji.class);
                        } else {
                            jiList = JSON.parseArray(ys.getGkdz(), Ji.class);
                        }
                        //将地址转为播放器集对象并添加集按钮
                        for (Ji ji : jiList) {
                            if (jiList.size() > 1) {
                                ysjis.addView(getJiBt(ji.getName()));
                            }
                        }
                        //播放第一个地址
                        if (jiList.size() > 1) {
                            FancyButton isbt = (FancyButton) ysjis.getFlexItemAt(playindex);
                            isbt.setBackgroundColor(getResources().getColor(R.color.Red1));
                        }
                        videoPlayer.setUp(jiList.get(playindex).getUrl(),false,"测试");
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
                        pm.setText("片名:" + ys.getPm());
                        lx.setText("类型:" + ys.getLx());
                        zt.setText("状态:" + ys.getZt());
                        dq.setText("地区:" + ys.getDq());
                        zy.setText("主演:" + ys.getZy());
                        dy.setText("导演:" + ys.getDy());
                        gxtime.setText("更新时间:" + ys.getGxtime());
                        sytime.setText("上映时间:" + ys.getSytime());
                        js.setText("介绍:" + ys.getJs());
                        MaterialRatingBar pf = findViewById(R.id.ysxx_pf);
                        pf.setRating(ys.getPf() / 2);
                        pf.setStepSize(0.1f);
                        if (ys.getPf() < 0.5) {
                            pf.setVisibility(View.GONE);
                        }

                    }
                });
    }
    private FancyButton getJiBt(String string) {
        FancyButton fancyButton = new FancyButton(this);
        fancyButton.setTextColor(getResources().getColor(R.color.db));
        fancyButton.setBackgroundColor(getResources().getColor(R.color.Orange1));
        fancyButton.setFocusBackgroundColor(getResources().getColor(R.color.Green1));
        fancyButton.setRadius(15);
        fancyButton.setIconPadding(10, 5, 10, 5);
        fancyButton.setText(string);
        fancyButton.setOnClickListener(v -> {
            FancyButton lastfb = (FancyButton) ysjis.getFlexItemAt(playindex);
            lastfb.setBackgroundColor(getResources().getColor(R.color.Orange1));
            FancyButton s = (FancyButton) v;
            for (Ji j : jiList) {
                if (j.getName().contentEquals(s.getText())) {
                    playindex = jiList.indexOf(j);
                   // videoPlayer.setUp(playindex);
                }
            }
            v.setBackgroundColor(getResources().getColor(R.color.Red1));
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fancyButton.getLayoutParams());
        lp.width = (screenWidth / 5) - 10;
        lp.setMargins(5, 10, 5, 10);
        fancyButton.setLayoutParams(lp);
        return fancyButton;
    }
    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        isDestory = true;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
    private void getDanmu() {

    }
    private GSYVideoPlayer getCurPlay() {
        if (videoPlayer.getFullWindowPlayer() != null) {
            return  videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
    }
}
