package com.inwaishe.app.dbroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;

/**
 * Created by WangJing on 2017/10/20.
 * xml 存储
 */

public class SharePreferencesStore {

    private static SharePreferencesStore mInstance;
    private static final String LOG_TAG = "SharePreferencesStore";
    private static final String APP_PREFS = "App_Prefs";

    private final Map<String,Object> infos = new HashedMap();
    private final SharedPreferences infoPrefs;

    public static SharePreferencesStore getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharePreferencesStore(context);
        }
        return mInstance;
    }

    private SharePreferencesStore(Context context){
        infoPrefs = context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE);
        Map<String, ?> prefsMap = infoPrefs.getAll();
        for(Map.Entry<String,?> entry: prefsMap.entrySet()){
            infos.put(entry.getKey(),entry.getValue());
        }
    }
    /**
     * 添加String
     * @param key
     * @param value
     */
    public void add(String key,String value){
        Log.i(LOG_TAG,"add " + key + ":" + value);
        SharedPreferences.Editor editor = infoPrefs.edit();
        if(infoPrefs.contains(key)){
            editor.remove(key);
        }
        editor.putString(key,value);
        editor.apply();
        if(infos.containsKey(key)){
            infos.remove(key);
        }
        infos.put(key,value);
    }

    /**
     * 添加Boolean
     * @param key
     * @param value
     */
    public void add(String key,boolean value){
        Log.i(LOG_TAG,"add " + key + ":" + value);
        SharedPreferences.Editor editor = infoPrefs.edit();
        if(infoPrefs.contains(key)){
            editor.remove(key);
        }
        editor.putBoolean(key,value);
        editor.apply();
        if(infos.containsKey(key)){
            infos.remove(key);
        }
        infos.put(key,value);
    }

    /**
     * 添加Long
     * @param key
     * @param value
     */
    public void add(String key,long value){
        Log.i(LOG_TAG,"add " + key + ":" + value);
        SharedPreferences.Editor editor = infoPrefs.edit();
        if(infoPrefs.contains(key)){
            editor.remove(key);
        }
        editor.putLong(key,value);
        editor.apply();
        if(infos.containsKey(key)){
            infos.remove(key);
        }
        infos.put(key,value);
    }

    /**
     * 添加Float
     * @param key
     * @param value
     */
    public void add(String key,float value){
        Log.i(LOG_TAG,"add " + key + ":" + value);
        SharedPreferences.Editor editor = infoPrefs.edit();
        if(infoPrefs.contains(key)){
            editor.remove(key);
        }
        editor.putFloat(key,value);
        editor.apply();
        if(infos.containsKey(key)){
            infos.remove(key);
        }
        infos.put(key,value);
    }

    /**
     * 获取保存的值
     * @param key
     * @return
     */
    public Object get(String key){
        return infos.get(key);
    }

    /**
     * 删除保存的元素
     * @param key
     */
    public void delete(String key){
        SharedPreferences.Editor editor = infoPrefs.edit();
        if(infoPrefs.contains(key)){
            editor.remove(key);
            editor.apply();
        }
        if(infos.containsKey(key)){
            infos.remove(key);
        }
    }

}
