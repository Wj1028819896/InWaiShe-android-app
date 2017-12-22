package com.inwaishe.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inwaishe.app.R;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.common.DialogUtils;
import com.inwaishe.app.common.HashUtils;
import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.dbroom.SharePreferencesStore;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.inwaishe.app.framework.activitytrans.EnterActivityTool;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.http.OkCallback;
import com.inwaishe.app.http.OkHttpUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ImageView mIvLogo;
    private EditText mEtAccount;
    private TextInputEditText mTietPassword;
    private TextInputLayout mTLUserName,mTlPassWord;
    private Button mBtnLogin,mBtnClose;
    SharePreferencesStore sharePreferencesStore;
    String action="",hash="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initEvent();
        sharePreferencesStore = SharePreferencesStore.getInstance(this);
        EnterActivityTool
                .getEnterTransition(this,mIvLogo,savedInstanceState)
                .setAnimatorDuration(300)
                .setTimeInterpolator(new DecelerateInterpolator())
                .startAnimate();
    }

    private void initView() {

        mIvLogo = (ImageView) findViewById(R.id.ivLogo);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnClose = (Button) findViewById(R.id.btnClose);
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mTietPassword = (TextInputEditText) findViewById(R.id.tiet_password);
        mTLUserName = (TextInputLayout) findViewById(R.id.til_account);
        mTlPassWord = (TextInputLayout) findViewById(R.id.til_password);

        mTLUserName.setHint("用户名");
        mTlPassWord.setHint("密码");

    }

    private void initEvent() {

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mTLUserName.getEditText().getText().toString();
                String password = mTlPassWord.getEditText().getText().toString();

                if(TextUtils.isEmpty(username)){
                    mTLUserName.setError("用户名不能为空");
                    return;
                }else if(TextUtils.isEmpty(password)){
                    mTlPassWord.setError("密码不能为空");
                    return;
                }
                testLogin();
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String,String> maps = new DataProvider().getLoginPageFormInfo();
                    LoginActivity.this.action = maps.get("action");
                    LoginActivity.this.hash = maps.get("formhash");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void testLogin() {

        DialogUtils.showProgressDialog(this,"登录中...");

        String url = action;
        String referer = "http://www.inwaishe.com/";
        String loginfield = "username";
        String username = mEtAccount.getText().toString();
        String password = mTietPassword.getText().toString();
        String questionid = "0";
        String answer = "";
        Map<String,String> formes = new ArrayMap<>();
        formes.put("formhash",hash);
        formes.put("referer",referer);
        formes.put("password",password);
        formes.put("loginfield",loginfield);
        formes.put("username",username);
        formes.put("questionid",questionid);
        formes.put("answer",answer);

        OkHttpUtils.make(this).formRequest(url,false,formes,new OkCallback(){
            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                if (response.isSuccessful()){

                    final String back = response.body().string();
                    if(back.contains("欢迎")){
                        int dex01 = back.indexOf("登录前页面");
                        int start = back.indexOf("{",dex01);
                        int end = back.indexOf("}",start) + 1;
                        String json = back.substring(start,end);

                        Headers headers = response.headers();
                        HttpUrl loginUrl = call.request().url();
                        //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                        List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                        StringBuilder cookieStr = new StringBuilder();
                        for(Cookie cookie : cookies){
                            cookieStr.append(cookie.name()).append("=").append(cookie.value()+";");
                        }
                        //防止header没有Cookie的情况
                        if (cookies != null){
                            //存储到Cookie管理器中
                            OkHttpUtils.make(LoginActivity.this).okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);//这样就将Cookie存储到缓存中了
                        }

                        JSONObject ob =  JSON.parseObject(json);
                        String uid = ob.getString("uid");

                        saveUserInfo(uid);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.close();
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }else{
                        int start = back.indexOf("登录失败");
                        if(start == -1){
                            start = back.indexOf("密码错误");
                        }
                        final int end = back.indexOf("<script",start);
                        final String error = back.substring(start,end);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.close();
                                Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 用户信息直接保存
     * @param uid
     */
    private void saveUserInfo(String uid){
        UserInfo info = null;
        try {
            info = new DataProvider().getUesrInfoFromUidSpace(uid);
            String str = JSON.toJSONString(info);
            sharePreferencesStore.add(CommonData.UID,str);
            XBus.getInstance().post(""+CommonData.REQUESTCODE_LOGIN,info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
