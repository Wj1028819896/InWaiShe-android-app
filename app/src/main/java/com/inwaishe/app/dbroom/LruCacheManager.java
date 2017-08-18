package com.inwaishe.app.dbroom;

import android.support.v4.util.LruCache;

import org.jsoup.nodes.Document;

/**
 * Created by Administrator on 2017/8/12 0012.
 *
 * 内存缓存
 */

public class LruCacheManager {


    private static LruCacheManager mInstance;

    private LruCache<String,Document> lruCacheHtml;//网页Html文字缓存
    private LruCacheManager(){
        lruCacheHtml = new LruCache<>(30);
    }
    public static LruCacheManager getInstance(){

        if(mInstance == null){
            mInstance = new LruCacheManager();
        }
        return mInstance;
    }

    public void putHtmlDocment(String key,Document document){
        lruCacheHtml.put(key,document);
    }

    public Document getHtmlDocment(String key){
        return lruCacheHtml.get(key);
    }
}
