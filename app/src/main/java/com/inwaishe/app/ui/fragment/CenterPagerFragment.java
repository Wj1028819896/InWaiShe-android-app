package com.inwaishe.app.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.inwaishe.app.R;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.dbroom.SharePreferencesStore;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.inwaishe.app.framework.activitytrans.FromActivityTool;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.http.PreferencesCookieStore;
import com.inwaishe.app.ui.AboutUsActivity;
import com.inwaishe.app.ui.LoginActivity;
import com.inwaishe.app.ui.MyApplication;
import com.inwaishe.app.widget.SettingItemTag;

import static com.inwaishe.app.common.CommonData.REQUESTCODE_LOGIN;

/**
 * 个人中心
 * Created by WangJing on 2017/9/15.
 */

public class CenterPagerFragment extends LazyFragment {
    public static final String TAG = "CenterPagerFragment";

    private SettingItemTag mStNightModel,mStAboutUs;
    private ImageView mIvUsrIcon,mIvUsrIconNoLogin;
    private Button mBtnLogin,mBtnLoginOut;
    private TextView mTvUserStatus;
    private RelativeLayout mRlLogin,mRlNoLogin;
    private TextView mTvUsrName,mTvUsrLevel;
    private SharePreferencesStore sharePreferencesStore;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        initView(rootview);
        initData();
        initEvent();
        return rootview;
    }

    private void initData() {
        sharePreferencesStore = SharePreferencesStore.getInstance(getContext());
        Object uid = sharePreferencesStore.get(CommonData.UID);
        if(uid == null){
            //未登陆
            mRlLogin.setVisibility(View.GONE);
            mRlNoLogin.setVisibility(View.VISIBLE);
            mBtnLoginOut.setVisibility(View.GONE);
        }else{
            //已经登陆
            UserInfo info = JSON.parseObject((String)uid,UserInfo.class);

            mTvUserStatus.setText("" + info.userFireValue.replace("燃值","燃值:")
                    + " " + info.userBp.replace("积分","积分:"));
            mTvUsrLevel.setText(info.userGroup);
            mTvUsrName.setText("" + info.userName);
            GlideUtils.disPlayUrl(getActivity(),info.userIcon,mIvUsrIcon);
            upDataUsrView(info.uid);
            mRlLogin.setVisibility(View.VISIBLE);
            mRlNoLogin.setVisibility(View.GONE);
            mBtnLoginOut.setVisibility(View.VISIBLE);
        }
    }

    private void upDataView(){
        Object uid = sharePreferencesStore.get(CommonData.UID);
        if(uid == null){
            //未登陆
            mRlLogin.setVisibility(View.GONE);
            mRlNoLogin.setVisibility(View.VISIBLE);
            mBtnLoginOut.setVisibility(View.GONE);
        }else{
            //已经登陆
            UserInfo info = JSON.parseObject((String)uid,UserInfo.class);

            mTvUserStatus.setText("" + info.userFireValue.replace("燃值","燃值:")
                    + " " + info.userBp.replace("积分","积分:"));
            mTvUsrLevel.setText(info.userGroup);
            mTvUsrName.setText("" + info.userName);
            GlideUtils.disPlayUrl(getActivity(),info.userIcon,mIvUsrIcon);
            mRlLogin.setVisibility(View.VISIBLE);
            mRlNoLogin.setVisibility(View.GONE);
            mBtnLoginOut.setVisibility(View.VISIBLE);
        }
    }

    private void initView(View rootview) {

        mStNightModel = (SettingItemTag) rootview.findViewById(R.id.stNightModel);
        mStAboutUs = (SettingItemTag) rootview.findViewById(R.id.stAboutUs);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.con);
        boolean isNight = sharedPreferences.getBoolean("THEME_NIGHT",false);
        mStNightModel.setRightSwitchState(isNight);
        mIvUsrIcon = (ImageView) rootview.findViewById(R.id.ivUsrIcon);
        mTvUsrName = (TextView) rootview.findViewById(R.id.tvUsrName);
        mTvUsrLevel = (TextView) rootview.findViewById(R.id.tvUsrLevel);
        mBtnLogin = (Button) rootview.findViewById(R.id.btnLogin);
        mBtnLoginOut = (Button) rootview.findViewById(R.id.btnLoginOut);
        mTvUserStatus = (TextView) rootview.findViewById(R.id.tvUserStatus);
        mRlLogin = (RelativeLayout) rootview.findViewById(R.id.rlLogin);
        mRlNoLogin = (RelativeLayout) rootview.findViewById(R.id.rlNoLogin);
        mIvUsrIconNoLogin = (ImageView) rootview.findViewById(R.id.ivUsrIconNoLogin);

    }

    private void initEvent() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(), LoginActivity.class);
                FromActivityTool.laucherWithShareView(getActivity(),it,mIvUsrIconNoLogin);
            }
        });

        mBtnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除数据
                sharePreferencesStore.delete(CommonData.UID);
                PreferencesCookieStore preferencesCookieStore = PreferencesCookieStore.getInstance(getActivity());
                preferencesCookieStore.removeAll();
                //
                upDataView();
            }
        });

        XBus.getInstance().observe(this,""+CommonData.REQUESTCODE_LOGIN, new XBusObserver<UserInfo>() {
            @Override
            public void onCall(UserInfo info) {
                upDataView();
            }
        }).runOn(XBusThreadModel.mainThread());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upDataUsrView(final String uid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UserInfo info = new DataProvider().getUesrInfoFromUidSpace(uid);
                    String str = JSON.toJSONString(info);
                    sharePreferencesStore.add(CommonData.UID,str);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRlLogin.setVisibility(View.VISIBLE);
                            mRlNoLogin.setVisibility(View.GONE);
                            mTvUserStatus.setText("" + info.userFireValue.replace("燃值","燃值:")
                                    + " " + info.userBp.replace("积分","积分:"));
                            mTvUsrLevel.setText(info.userGroup);
                            mTvUsrName.setText("" + info.userName);
                            GlideUtils.disPlayUrl(getActivity(),info.userIcon,mIvUsrIcon);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_centerpager;
    }

    @Override
    public void finishCreateView(Bundle state) {


        mStNightModel.setmOnLSettingItemClick(new SettingItemTag.OnLSettingItemClick(){

            @Override
            public void click(boolean isChecked) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.con);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(!isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.putBoolean("THEME_NIGHT",isChecked);
                editor.commit();
                getActivity().recreate();
            }
        });

        mStAboutUs.setmOnLSettingItemClick(new SettingItemTag.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                Toast.makeText(getActivity(),"关于我们",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
