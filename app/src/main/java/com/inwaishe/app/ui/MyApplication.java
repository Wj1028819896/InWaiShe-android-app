package com.inwaishe.app.ui;

import android.app.Application;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.squareup.leakcanary.LeakCanary;


import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/8/31 0031.
 */

public class MyApplication extends Application implements LifecycleRegistryOwner{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Context con;
    public String mBmobAppId = "a7089f750d8e73179eb9997bfd59cf7e";

    @Override
    public void onCreate() {
        super.onCreate();
        con = this;
        /**DAYNIGHT THEME init**/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("THEME_NIGHT",false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        /**BMOB init**/
//        BmobConfig bmobConfig = new BmobConfig.Builder(this)
//                //设置appkey
//                .setApplicationId(mBmobAppId)
//                //请求超时时间（单位为秒）：默认15s
//                .setConnectTimeout(30)
//                //文件分片上传时每片的大小（单位字节），默认512*1024
//                .setUploadBlockSize(1024*1024)
//                //文件的过期时间(单位为秒)：默认1800s
//                .setFileExpiration(2500)
//                .build();
//        Bmob.initialize(bmobConfig);
        /**init LeakCandy**/
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        //LeakCanary.install(this);
    }
    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
