package com.inwaishe.app.framework.emotionkeyboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.EmotionGroupFragment;
import com.inwaishe.app.framework.emotionkeyboard.model.EmotionGroupCofig;

import java.util.ArrayList;

/**
 * Created by WangJing on 2017/12/7.
 * 表情组选择Adapter
 */

public class EmotionGroupFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<EmotionGroupCofig> mEmotionGroupConfigs;
    public EmotionGroupFragmentPagerAdapter(FragmentManager fm,ArrayList<EmotionGroupCofig> emotionGroupCofigs) {
        super(fm);
        this.mEmotionGroupConfigs = emotionGroupCofigs;
    }

    @Override
    public Fragment getItem(int i) {
        return EmotionGroupFragment.newInstance(mEmotionGroupConfigs.get(i));
    }

    @Override
    public int getCount() {
        return mEmotionGroupConfigs.size();
    }
}
