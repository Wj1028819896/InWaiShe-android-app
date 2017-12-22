package com.inwaishe.app.http;

import android.content.Context;

import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by WangJing on 2017/10/13.
 * okhttp3 封装类
 */

public class OkHttpUtils {

    private static OkHttpUtils okHttpUtils;
    public OkHttpClient okHttpClient;
    public static OkHttpUtils make(Context context){
        if(okHttpUtils == null){
            okHttpUtils = new OkHttpUtils(context);
        }
        return okHttpUtils;
    }

    private OkHttpUtils(final Context context){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.cookieJar(new OkCookieJar(context));
        okHttpClient = builder.build();
    }

    public void formRequest(String action,boolean useCookie,Map<String,String> formMaps,OkCallback callback){
        FormBody.Builder formbuilder = new FormBody.Builder();
        for(Map.Entry<String,String> entry : formMaps.entrySet()){
            formbuilder.add(entry.getKey(),entry.getValue());
        }
        RequestBody requestBody = formbuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(action)
                .post(requestBody);

        if(useCookie){
            StringBuilder cookieStr = new StringBuilder();
            List<Cookie> cookies = ((OkCookieJar)okHttpClient.cookieJar()).getLoginCookies("www.inwaishe.com");
            for(Cookie cookie : cookies){
                cookieStr.append(cookie.name()).append("=").append(cookie.value()+";");
            }
            builder.header("Cookie",cookieStr.toString());
        }else{

        }
        Call call = okHttpClient.newCall(builder.build());
        call.enqueue(callback);

    }

}
