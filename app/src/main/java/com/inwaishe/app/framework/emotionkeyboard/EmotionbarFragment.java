package com.inwaishe.app.framework.emotionkeyboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.inwaishe.app.R;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.adapter.EmotionGroupFragmentPagerAdapter;
import com.inwaishe.app.framework.emotionkeyboard.model.EmotionGroupCofig;

import java.util.ArrayList;

/**
 * Created by WangJing on 2017/12/7.
 * 表情面版根布局
 */

public class EmotionbarFragment extends Fragment{

    private View mRootView;
    private ViewPager mVpSelectEmotionGroup;
    //private RadioGroup mRgSelectEmotionGroup;
    private LinearLayout mHsv;
    private EmotionGroupFragmentPagerAdapter mAdapter;
    private ArrayList<EmotionGroupCofig> mEmotionGroupConfigs;
    private Context mContxet;
    public static EmotionbarFragment newInstance(ArrayList<EmotionGroupCofig> emotionGroupCofigs){
        EmotionbarFragment fragment = new EmotionbarFragment();
        Bundle bd = new Bundle();
        bd.putParcelableArrayList("EMOTIONGROUPCOFIGS",emotionGroupCofigs);
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 获得表情组配置数据 */
        mEmotionGroupConfigs = getArguments().getParcelableArrayList("EMOTIONGROUPCOFIGS");
        mContxet = getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.emotion_layout,container,false);
        initView(mRootView);
        initData();
        initEvent();
        return mRootView;
    }

    private void initView(View mRootView) {
        mVpSelectEmotionGroup = (ViewPager) mRootView.findViewById(R.id.vpEmotionGroupSelect);
        //mRgSelectEmotionGroup = (RadioGroup) mRootView.findViewById(R.id.rgSelectEmotion);
        mHsv = (LinearLayout) mRootView.findViewById(R.id.llSelectGroupTag);
    }

    private void initData() {

        for(int p = 0; p < mEmotionGroupConfigs.size(); p++){
            addButtonToHsv(mEmotionGroupConfigs.get(p),p);
        }

        FragmentManager fm = getChildFragmentManager();
        mAdapter = new EmotionGroupFragmentPagerAdapter(fm,mEmotionGroupConfigs);
        mVpSelectEmotionGroup.setAdapter(mAdapter);
        mVpSelectEmotionGroup.setCurrentItem(0);
    }

    private void addButtonToHsv(EmotionGroupCofig emotionGroupCofig, final int p) {
        final ImageView imageView = new ImageView(mContxet);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(emotionGroupCofig.resId);
        if(p == 0){
            imageView.setBackgroundResource(R.color.itemSelected);//背景
        }else{
            imageView.setBackgroundResource(R.color.itemNormal);//背景
        }
        int dp10 = AppUtils.dip2px(mContxet,10);
        imageView.setPadding(dp10,dp10,dp10,dp10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AppUtils.dip2px(mContxet,40), ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVpSelectEmotionGroup.setCurrentItem(p);
            }
        });
        mHsv.addView(imageView,p);
    }

    private void selectPosition(int p) {
        resetButtonBg();
        View v = mHsv.getChildAt(p);
        if(v instanceof ImageView){
            ((ImageView) v).setBackgroundResource(R.color.itemSelected);
        }
    }

    private void resetButtonBg(){
        int cnum = mHsv.getChildCount();
        for(int p = 0;p < cnum; p++){
            View v = mHsv.getChildAt(p);
            if(v instanceof ImageView){
                ((ImageView) v).setBackgroundResource(R.color.itemNormal);
            }
        }
    }

    private void initEvent() {

        mVpSelectEmotionGroup.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectPosition(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
}
