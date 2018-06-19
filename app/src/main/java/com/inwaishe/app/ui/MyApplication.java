package com.inwaishe.app.ui;

import android.app.Application;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.alibaba.fastjson.JSON;
import com.inwaishe.app.BuildConfig;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.dbroom.SharePreferencesStore;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/8/31 0031.
 */

public class MyApplication extends Application implements LifecycleRegistryOwner{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Context con;

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
        /**initBugly***/
        CrashReport.initCrashReport(this.getApplicationContext(),"ec84367dc9",true);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        SharePreferencesStore sharePreferencesStore = SharePreferencesStore.getInstance(this);
        Object uid = sharePreferencesStore.get(CommonData.UID);
        if(uid != null){
            UserInfo info = JSON.parseObject((String)uid,UserInfo.class);
            CrashReport.setUserId(info.uid);//bugly userid
        }

    }
    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
