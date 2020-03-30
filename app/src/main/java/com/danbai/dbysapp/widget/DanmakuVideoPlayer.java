package com.danbai.dbysapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cdnbye.sdk.P2pEngine;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.entity.Ji;
import com.github.mummyding.colorpickerdialog.ColorPickerDialog;
import com.github.mummyding.colorpickerdialog.OnColorChangedListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;

import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;

public class DanmakuVideoPlayer extends StandardGSYVideoPlayer {
    private BaseDanmakuParser mParser;//解析器对象
    public IDanmakuView mDanmakuView;//弹幕view
    private DanmakuContext mDanmakuContext;
    public TextView mSendDanmaku, mToogleDanmaku, dmcolor, dmtype;
    public EditText dminput;
    private long mDanmakuStartSeekPosition = -1;
    private boolean mDanmaKuShow = true;
    public List<Ji> playlist;
    public int playindex;
    public boolean cache = false;
    public int ysid;
    public String tagpm;
    int[] colors = new int[]{Color.YELLOW, Color.BLACK, Color.BLUE, Color.GRAY,
            Color.GREEN, Color.CYAN, Color.RED, Color.DKGRAY, Color.LTGRAY, Color.MAGENTA,
            Color.rgb(100, 22, 33), Color.rgb(82, 182, 2), Color.rgb(122, 32, 12), Color.rgb(82, 12, 2),
            Color.rgb(89, 23, 200), Color.rgb(13, 222, 23), Color.rgb(222, 22, 2), Color.rgb(2, 22, 222)};
    String[] dmtypes = new String[]{"滑过", "顶部", "底部"};

    public DanmakuVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public DanmakuVideoPlayer(Context context) {
        super(context);
    }

    public DanmakuVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        DanmakuVideoPlayer sf = (DanmakuVideoPlayer) from;
        DanmakuVideoPlayer st = (DanmakuVideoPlayer) to;
        st.playlist = sf.playlist;
        st.playindex = sf.playindex;
    }

    @Override
    public int getLayoutId() {
        return R.layout.danmaku_layout;
    }

    public boolean setUp(List<Ji> list, boolean cache, int index) {
        if (list != null & list.size() > 0) {
            playlist = list;
            playindex = index;
            this.cache = cache;
            return super.setUp(P2pEngine.getInstance().parseStreamUrl(playlist.get(index).getUrl()), cache, playlist.get(index).getName());
        }
        return false;
    }

    public boolean setUp(int index) {
        if (playlist != null & playlist.size() > 0) {
            mSaveChangeViewTIme = 0;
            playindex = index;
            return super.setUp(P2pEngine.getInstance().parseStreamUrl(playlist.get(index).getUrl()), cache, playlist.get(index).getName());
        }
        return false;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void init(Context context) {
        super.init(context);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        mSendDanmaku = (TextView) findViewById(R.id.send_danmaku);
        mToogleDanmaku = (TextView) findViewById(R.id.toogle_danmaku);
        dmcolor = (TextView) findViewById(R.id.dmcolor);
        dmtype = (TextView) findViewById(R.id.dmtype);
        dminput = (EditText) findViewById(R.id.dminput);
        dminput.setTextColor(Color.WHITE);
        dminput.setHintTextColor(Color.RED);
        //初始化弹幕显示
        initDanmaku();
        mSendDanmaku.setOnClickListener(this);
        mToogleDanmaku.setOnClickListener(this);
        dmcolor.setOnClickListener(this);
        dmtype.setOnClickListener(this);
    }

    @Override
    public void onVideoPause() {
        super.onVideoPause();
        danmakuOnPause();
    }

    @Override
    public void onVideoResume(boolean isResume) {
        super.onVideoResume(isResume);
        danmakuOnResume();
    }

    @Override
    protected void clickStartIcon() {
        super.clickStartIcon();
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            danmakuOnResume();
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            danmakuOnPause();
        }
    }

    @Override
    public void onCompletion() {
        releaseDanmaku(this);
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
        int time = mProgressBar.getProgress() * getDuration() / 100;
        //如果已经初始化过的，直接seek到对于位置
        if (mHadPlay && getDanmakuView() != null && getDanmakuView().isPrepared()) {
            resolveDanmakuSeek(this, time);
        } else if (mHadPlay && getDanmakuView() != null && !getDanmakuView().isPrepared()) {
            //如果没有初始化过的，记录位置等待
            setDanmakuStartSeekPosition(time);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.send_danmaku:
                if (dminput.getText().length() > 0) {
                    String t = null;
                    switch (dmtype.getText().toString()) {
                        case "滑过":
                            t = "0";
                            break;
                        case "顶部":
                            t = "1";
                            break;
                        case "底部":
                            t = "2";
                            break;
                    }
                    int type = 0;
                    switch (Integer.valueOf(t)) {
                        case 0:
                            type = 1;
                            break;
                        case 1:
                            type = 5;
                            break;
                        case 2:
                            type = 4;
                            break;
                    }
                    addDanmaku(type, String.valueOf(dminput.getText()),mDanmakuView.getCurrentTime() + 500, dmcolor.getCurrentTextColor(), true);
                    try {
                        QuietOkHttp.post("https://dm.dbys.vip/v3").addHeaders("Content-Type", " application/json;charset=UTF-8").addHeaders("Referer", "dbysapp")
                                .addParams("id", ysid + playlist.get(playindex).getName())
                                .addParams("author", "user")
                                .addParams("time", String.valueOf(Float.valueOf(getCurrentPositionWhenPlaying()) / 1000))
                                .addParams("text", String.valueOf(dminput.getText()))
                                .addParams("color", String.valueOf(dmcolor.getCurrentTextColor()))
                                .addParams("type", t).execute(new StringCallBack() {
                            @Override
                            protected void onSuccess(Call call, String response) {
                            }
                            @Override
                            public void onFailure(Call call, Exception e) {
                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    dminput.setText("");
                }
                break;
            case R.id.toogle_danmaku:
                mDanmaKuShow = !mDanmaKuShow;
                resolveDanmakuShow();
                break;
            case R.id.dmcolor:
                new ColorPickerDialog(getContext(), colors)
                        .setDismissAfterClick(false)
                        .setTitle("弹幕颜色选择")
                        .setCheckedColor(Color.BLACK)
                        .setOnColorChangedListener(new OnColorChangedListener() {
                            @Override
                            public void onColorChanged(int newColor) {
                                dmcolor.setTextColor(newColor);
                                dminput.setTextColor(newColor);
                            }
                        })
                        .build(6)
                        .show();
                break;
            case R.id.dmtype:
                TextView tv = (TextView) v;
                if (tv.getText().equals(dmtypes[0])) {
                    tv.setText(dmtypes[1]);
                } else if (tv.getText().equals(dmtypes[1])) {
                    tv.setText(dmtypes[2]);
                } else {
                    tv.setText(dmtypes[0]);
                }
        }
    }

    /**
     * 处理播放器在全屏切换时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            DanmakuVideoPlayer gsyVideoPlayer = (DanmakuVideoPlayer) gsyBaseVideoPlayer;
            //对弹幕设置偏移记录
            gsyVideoPlayer.setDanmakuStartSeekPosition(getCurrentPositionWhenPlaying());
            gsyVideoPlayer.setDanmaKuShow(getDanmaKuShow());
            onPrepareDanmaku(gsyVideoPlayer);
        }
        return gsyBaseVideoPlayer;
    }

    /**
     * 处理播放器在退出全屏时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            DanmakuVideoPlayer gsyDanmaVideoPlayer = (DanmakuVideoPlayer) gsyVideoPlayer;
            setDanmaKuShow(gsyDanmaVideoPlayer.getDanmaKuShow());
            if (gsyDanmaVideoPlayer.getDanmakuView() != null &&
                    gsyDanmaVideoPlayer.getDanmakuView().isPrepared()) {
                resolveDanmakuSeek(this, gsyDanmaVideoPlayer.getCurrentPositionWhenPlaying());
                resolveDanmakuShow();
                releaseDanmaku(gsyDanmaVideoPlayer);
            }
        }
    }

    protected void danmakuOnPause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    protected void danmakuOnResume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    private void initDanmaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuContext = DanmakuContext.create();
        if (mDanmakuView != null) {
            mParser = createParser();
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    if (getDanmakuView() != null) {
                        getDanmakuView().start();
                        if (getDanmakuStartSeekPosition() != -1) {
                            resolveDanmakuSeek(DanmakuVideoPlayer.this, getDanmakuStartSeekPosition());
                            setDanmakuStartSeekPosition(-1);
                        }
                        resolveDanmakuShow();
                    }
                }
            });
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        onPrepareDanmaku(this);
    }

    /**
     * 弹幕的显示与关闭
     */
    private void resolveDanmakuShow() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mDanmaKuShow) {
                    if (!getDanmakuView().isShown())
                        getDanmakuView().show();
                    mToogleDanmaku.setText("弹幕:开");
                } else {
                    if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }
                    mToogleDanmaku.setText("弹幕:关");
                }
            }
        });
    }

    /**
     * 开始播放弹幕
     */
    public void onPrepareDanmaku(DanmakuVideoPlayer gsyVideoPlayer) {
        if (gsyVideoPlayer.getDanmakuView() != null && !gsyVideoPlayer.getDanmakuView().isPrepared() && gsyVideoPlayer.getParser() != null) {
            gsyVideoPlayer.getDanmakuView().prepare(gsyVideoPlayer.getParser(),
                    gsyVideoPlayer.getDanmakuContext());
            QuietOkHttp.get("https://dm.dbys.vip/v3").addParams("id", ysid + playlist.get(playindex).getName()).execute(new StringCallBack() {
                @Override
                protected void onSuccess(Call call, String response) {
                    JSONArray data = JSON.parseObject(response).getJSONArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        JSONArray dm = data.getJSONArray(i);
                        Long time = (long) (dm.getFloat(0) * 1000);
                        int type = dm.getInteger(1);
                        switch (type) {
                            case 0:
                                type = 1;
                                break;
                            case 1:
                                type = 5;
                                break;
                            case 2:
                                type = 4;
                                break;
                        }
                        int color = dm.getInteger(2);
                        String text = dm.getString(4);
                        gsyVideoPlayer.addDanmaku(type, text, time, color, false);
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                }
            });
        }
    }

    /**
     * 弹幕偏移
     */
    private void resolveDanmakuSeek(DanmakuVideoPlayer gsyVideoPlayer, long time) {
        if (mHadPlay && gsyVideoPlayer.getDanmakuView() != null && gsyVideoPlayer.getDanmakuView().isPrepared()) {
            gsyVideoPlayer.getDanmakuView().seekTo(time);
        }
    }

    /**
     * 创建解析器对象，解析输入流
     *
     * @return
     */
    private BaseDanmakuParser createParser() {

        return new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
    }

    /**
     * 释放弹幕控件
     */
    private void releaseDanmaku(DanmakuVideoPlayer danmakuVideoPlayer) {
        if (danmakuVideoPlayer != null && danmakuVideoPlayer.getDanmakuView() != null) {
            Debuger.printfError("release Danmaku!");
            danmakuVideoPlayer.getDanmakuView().release();
        }
    }

    public BaseDanmakuParser getParser() {
        mParser = createParser();
        return mParser;
    }

    public DanmakuContext getDanmakuContext() {
        return mDanmakuContext;
    }

    public IDanmakuView getDanmakuView() {
        return mDanmakuView;
    }

    public long getDanmakuStartSeekPosition() {
        return mDanmakuStartSeekPosition;
    }

    public void setDanmakuStartSeekPosition(long danmakuStartSeekPosition) {
        this.mDanmakuStartSeekPosition = danmakuStartSeekPosition;
    }

    public void setDanmaKuShow(boolean danmaKuShow) {
        mDanmaKuShow = danmaKuShow;
    }

    public boolean getDanmaKuShow() {
        return mDanmaKuShow;
    }

    public void addDanmaku(int type, String text, long time, int color, boolean self) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(type);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示，所以提高等级
        danmaku.isLive = false;
        danmaku.setTime(time);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = color;
        if (self) {
            danmaku.borderColor = Color.RED;
        }
        mDanmakuView.addDanmaku(danmaku);
    }
}
