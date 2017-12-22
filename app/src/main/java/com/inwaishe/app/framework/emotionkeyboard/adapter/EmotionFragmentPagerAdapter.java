package com.inwaishe.app.framework.emotionkeyboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.EmotionFragment;

import java.util.ArrayList;

/**
 * Created by WangJing on 2017/12/7.
 * 表情页适配器
 */

public class EmotionFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mPagerNum;
    private ArrayList<Emotion> mEmotions;
    private ArrayList<EmotionFragment> mFragments = new ArrayList<>();

    public EmotionFragmentPagerAdapter(FragmentManager fm,int mPagerNum,ArrayList<Emotion> mEmotions) {
        super(fm);
        this.mEmotions = mEmotions;
        this.mPagerNum = mPagerNum;
        for(int i=0;i < mPagerNum; i++){
            int fromdex = i*20;
            int todex = 0;
            if(i != (mPagerNum-1)){
                todex = i*20 + 19;
            }else{
                todex = mEmotions.size()-1;
            }
            ArrayList<Emotion> data = new ArrayList<>();
            for(int p = fromdex; p <= todex; p++){
                data.add(mEmotions.get(p));
            }
            EmotionFragment fragment = EmotionFragment.newInstance(data);
            mFragments.add(fragment);
        }
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}
