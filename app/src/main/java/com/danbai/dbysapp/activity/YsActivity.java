package com.danbai.dbysapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.umeng.analytics.MobclickAgent;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;
import com.yanbo.lib_screen.callback.ControlCallback;
import com.yanbo.lib_screen.entity.ClingDevice;
import com.yanbo.lib_screen.entity.RemoteItem;
import com.yanbo.lib_screen.manager.ClingManager;
import com.yanbo.lib_screen.manager.ControlManager;
import com.yanbo.lib_screen.manager.DeviceManager;

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
    DanmakuVideoPlayer videoPlayer;
    Ysb ys;
    protected List<Ji> jiList = new ArrayList<>();
    int screenWidth;
    FlexboxLayout ysjis;

    //投屏
    private List<ClingDevice> clingDevices;
    private RemoteItem remoteItem;
    private Spinner spinner;
    private ArrayAdapter<String> adapter = null;
    private Button touping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys);
        videoPlayer = findViewById(R.id.player);
        spinner=findViewById(R.id.spinner);
        touping=findViewById(R.id.touping);
        init();
        ClingManager.getInstance().startClingService();
        tp();
    }

    private void tp(){
        //搜索设备
        clingDevices = DeviceManager.getInstance().getClingDeviceList();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item);
        //设置下拉列表风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器添加到spinner中去
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);//设置默认显示
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                for (int i=0;i<clingDevices.size();i++){
                    if(getDname(clingDevices.get(i)).equals(((TextView)arg1).getText())){
                        DeviceManager.getInstance().setCurrClingDevice(clingDevices.get(i));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        if(clingDevices.size()>0){
            for (int i=0;i<clingDevices.size();i++){
                adapter.add(getDname(clingDevices.get(i)));
            }
        }
        touping.setOnClickListener(b->{
            //设置网络投屏的信息
            RemoteItem itemurl = new RemoteItem("淡白影视", "996", "淡白影视",107362668, "00:00:00", "1920x1080",jiList.get(playindex).getUrl());
            //添加网络投屏的信息
            ClingManager.getInstance().setRemoteItem(itemurl);
            remoteItem = ClingManager.getInstance().getRemoteItem();
            //播放
            if (ControlManager.getInstance().getState() == ControlManager.CastState.STOPED) {
                newPlayCastRemoteContent();
            }
        });
    }
    /**
     * 网络投屏
     */
    private String getDname(ClingDevice device){
        return device.getDevice().getDetails().getFriendlyName()+"-"+device.getDevice().getDetails().getPresentationURI().getHost();
    }
    private void newPlayCastRemoteContent() {

        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);

        ControlManager.getInstance().newPlayCast(remoteItem, new ControlCallback() {

            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                ControlManager.getInstance().initScreenCastCallback();
                Log.d("投屏", "投屏成功");
            }

            @Override
            public void onError(int code, String msg) {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                Log.d("投屏", "投屏失败");
            }
        });
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
        videoPlayer.setDismissControlTime(60000);
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
            }
            @Override
            public void onAutoComplete(String url, Object... objects) {
                DanmakuVideoPlayer p = (DanmakuVideoPlayer) objects[1];
                if (p.playlist.size() > playindex + 1) {
                    if (p.setUp(playindex + 1)) {
                        playindex += 1;
                        playindex = p.playindex;
                        ysjis.getFlexItemAt(playindex).setBackgroundColor(getResources().getColor(R.color.Red1));
                        ysjis.getFlexItemAt(playindex - 1).setBackgroundColor(getResources().getColor(R.color.Orange1));
                        return;
                    }
                }
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
        videoPlayer.ysid = ysid;
        QuietOkHttp.get("https://dbys.vip/getys")
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
                        videoPlayer.tagpm = ys.getPm() + ys.getDy() + ys.getLx();
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
                        videoPlayer.setUp(jiList, false, playindex);
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
                    videoPlayer.setUp(playindex);
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
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
        MobclickAgent.onResume(this);
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
    private GSYVideoPlayer getCurPlay() {
        if (videoPlayer.getFullWindowPlayer() != null) {
            return videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
    }

}
