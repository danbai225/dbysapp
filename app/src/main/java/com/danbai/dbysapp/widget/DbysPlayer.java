package com.danbai.dbysapp.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.cdnbye.sdk.P2pEngine;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;

import java.util.List;

public class DbysPlayer extends ListGSYVideoPlayer {
    public List<GSYVideoModel> nojiasulist;
    public DbysPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public DbysPlayer(Context context) {
        super(context);
    }

    public DbysPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 播放指定集
     *
     * @return true表示还有下一集
     */
    public boolean playindiex(int i) {
        if (i < (mUriList.size())) {
            mPlayPosition = i;
            GSYVideoModel gsyVideoModel = nojiasulist.get(mPlayPosition);
            gsyVideoModel.setUrl(P2pEngine.getInstance().parseStreamUrl(gsyVideoModel.getUrl()));
            mUriList.set(i,gsyVideoModel);
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            startPlayLogic();
            return true;
        }
        return false;
    }
    public int getPlayindex(){
        return mPlayPosition;
    }
    @Override
    public boolean playNext(){
        if (mPlayPosition < (mUriList.size() - 1)) {
            mPlayPosition += 1;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            Log.d("淡白123","12");
            gsyVideoModel.setUrl(P2pEngine.getInstance().parseStreamUrl(gsyVideoModel.getUrl()));
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            startPlayLogic();
            return true;
        }
        return false;
    }
}
