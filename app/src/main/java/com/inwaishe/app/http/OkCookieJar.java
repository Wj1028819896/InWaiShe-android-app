package com.inwaishe.app.http;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by WangJing on 2017/10/19.
 *
 * OkHttp3 Cookiejar
 *
 */

public class OkCookieJar implements CookieJar {

    PreferencesCookieStore cookieStore;
    public OkCookieJar(Context context){
       cookieStore = PreferencesCookieStore.getInstance(context);
    }
    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        if (list != null && list.size() > 0) {
            for (Cookie item : list) {
                cookieStore.add(httpUrl, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl);
        return cookies;
    }

    public List<Cookie> getLoginCookies(String host){
        HttpUrl httpUrl = new HttpUrl.Builder()
                .host(host)
                .scheme("http")
                .build();
        List<Cookie> cookies = cookieStore.get(httpUrl);
        return cookies;
    }
}
