package com.inwaishe.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.ui.fragment.SocialShareDialogFragment;
import com.inwaishe.app.widget.SettingItemTag;

public class SettingActivity extends BaseActivity {

    SettingItemTag mStClearCache;
    SettingItemTag mStShareApp;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mStClearCache = (SettingItemTag) findViewById(R.id.stClearCache);
        mStShareApp = (SettingItemTag) findViewById(R.id.stShareApp);

    }

    private void initData() {
        mContext = this;
        mStClearCache.setRightText("" + GlideUtils.getCacheSize(this));

    }

    private void initEvent() {
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mStClearCache.setmOnLSettingItemClick(new SettingItemTag.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                GlideUtils.clear(SettingActivity.this);
                mStClearCache.setRightText("");
                Toast.makeText(SettingActivity.this,"清除成功",Toast.LENGTH_LONG).show();
            }
        });

        mStShareApp.setmOnLSettingItemClick(new SettingItemTag.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                SocialShareDialogFragment socialShareDialogFragment = new SocialShareDialogFragment();
                socialShareDialogFragment.setEventCallBack(new SocialShareDialogFragment.EventCallBack() {
                    @Override
                    public void callBack(int eventCode) {
                        switch (eventCode){
                            case SocialShareDialogFragment.EVENT_COPYLIK:
                                AppUtils.copyToClipeBoard(mContext,"inwaishe下载地址：" + "https://fir.im/inwaishe" );
                                Toast.makeText(mContext,"成功复制到粘贴板",Toast.LENGTH_SHORT).show();
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXCIRCLE:
                                AppUtils.shareWebPageToWxCircle(mContext,null,"In外设,好用app分享","https://fir.im/inwaishe","第一时间，知晓外设资讯....点击下载>>");
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXFRIENDS:
                                AppUtils.shareWebPageToWxFriends(mContext,null,"In外设,好用app分享","https://fir.im/inwaishe","第一时间，知晓外设资讯....点击下载>>");
                                break;
                        }
                    }
                });
                socialShareDialogFragment.show(getFragmentManager(),"SocialShareDialogFragment");
            }
        });
    }
}
