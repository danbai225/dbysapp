package com.danbai.dbysapp;

import android.app.Application;

import com.cdnbye.sdk.P2pEngine;

public class dbysApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        P2pEngine.initEngine(this, "_WJjufJZR", null);
    }
}
