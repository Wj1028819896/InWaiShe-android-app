package com.inwaishe.app.media;

import android.app.Activity;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.inwaishe.app.R;
import com.inwaishe.app.common.MediaUtils;
import com.inwaishe.app.entity.video.BiliVideoInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.http.downloadfile.DownLoad;

import java.io.File;
import java.io.IOException;

/***
 * 媒体播放界面
 */
public class MediaPlayerActivity extends AppCompatActivity implements LifecycleRegistryOwner {
    public static final int TYPE_ANDROID_MEDIA = 0x001;
    public static final int TYPE_YOUKU_WEBVIEW = 0x002;
    public static final int TYPE_IJKPLAYER = 0x003;
    public static final String MEDIA_TYPE = "media_type";
    private View ViewRoot;
    private Context mContext;
    /*android player*/
    MediaPlayer mMediaPlayer;
    VideoView mVedioView;
    SurfaceHolder mSurfaceHolder;
    /*youku Webview*/
    WebView mWebView;
    /*ijkplayer*/
    PlayerView mIjkPlayerView;
    /*intent*/
    String avid = "";
    int type = -1;
    String youkuUrl = "";
    /*XBus*/
    private String mXTaskTag = "getiVideoUrl";
    Runnable mGetVideoUrlTask;
    XBusObserver mGetVideoUrlXBusObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra(MEDIA_TYPE,TYPE_ANDROID_MEDIA);
        avid = getIntent().getStringExtra("AID");
        youkuUrl = getIntent().getStringExtra("YOUKU");
        mContext = this;
        switch (type){
            case TYPE_ANDROID_MEDIA:
                ViewRoot = creatMediaPlayerView(this);
                break;
            case TYPE_YOUKU_WEBVIEW:
                ViewRoot = creatYoukuWebView(this);
                break;
            case TYPE_IJKPLAYER:
                ViewRoot = creatIjkPlayer(this);
                break;
        }
        setContentView(ViewRoot);
        initData();
        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWebView != null){
            mWebView.onPause();
        }
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
        if(mIjkPlayerView != null){
            mIjkPlayerView.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebView != null){
            mWebView.destroy();
        }
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if(mIjkPlayerView != null){
            mIjkPlayerView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWebView != null){
            mWebView.onResume();
        }
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
        }
        if(mIjkPlayerView != null){
            mIjkPlayerView.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
    }

    private void initData() {
        mGetVideoUrlXBusObserver = new XBusObserver<BiliVideoInfo>(){
            @Override
            public void onCall(BiliVideoInfo var) {
                if(var != null){
                    if(type == TYPE_ANDROID_MEDIA){
                        //Android 自带
                        try {
                            mMediaPlayer = MediaPlayer.create(MediaPlayerActivity.this, Uri.parse(var.url),mSurfaceHolder);
                            //mMediaPlayer.setDataSource(var.url);
                            mMediaPlayer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(type == TYPE_IJKPLAYER){
                        //ijkplayer
                        mIjkPlayerView = new PlayerView(MediaPlayerActivity.this)
                                .setTitle("")
                                .setScaleType(PlayStateParams.fitparent)
                                .hideMenu(true)
                                .forbidTouch(false)
                                .setTitle(var.title)
                                .setPlaySource(var.url)
                                .startPlay();
                        mIjkPlayerView.setBrightness(50);
                    }
                }
            }
        };
        mGetVideoUrlTask = new Runnable() {
            @Override
            public void run() {
                try {
                    BiliVideoInfo biliVideoInfo = DownLoad.getFirstVideo(avid);
                    XBus.getInstance().post(mXTaskTag,biliVideoInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initEvent() {
        XBus.getInstance().observe(this,mXTaskTag,mGetVideoUrlXBusObserver).runOn(XBusThreadModel.mainThread());
        if(type == TYPE_YOUKU_WEBVIEW){
            mWebView.loadUrl(youkuUrl);
        }else{
            new Thread(mGetVideoUrlTask).start();
        }
    }

    private View creatYoukuWebView(Context con){
        mWebView = new WebView(con);
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
        }
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT > 20){
            mWebView.setTransitionGroup(true);
        }
        mWebView.setBackgroundColor(0);
        return mWebView;
    }

    private View creatMediaPlayerView(Context con) {
        mVedioView =  new VideoView(con);
        mSurfaceHolder = mVedioView.getHolder();

        return mVedioView;
    }

    private View creatIjkPlayer(Context con) {
        View v = LayoutInflater.from(con).inflate(R.layout.simple_player_view_player,null,false);
        return v;
    }

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}
