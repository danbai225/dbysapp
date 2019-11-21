package com.danbai.dbysapp;

import android.app.Application;

import com.cdnbye.sdk.P2pEngine;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.umeng.commonsdk.UMConfigure;

public class dbysApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        P2pEngine.initEngine(this, "_WJjufJZR", null);
        UMConfigure.init(this,"5dd51f3e3fc1957eb5000c6c", "danbai",UMConfigure.DEVICE_TYPE_PHONE,null);
        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();
    }
}
