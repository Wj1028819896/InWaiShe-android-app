package com.inwaishe.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.inwaishe.app.R;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.dbroom.NewsTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WangJing on 2017/8/14.
 * 评测资讯页
 */

public class NewsPagerFragment extends LazyFragment{
    public static final String TAG = "NewsPagerFragment";
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<String> mTitles = new ArrayList<>();
    private HashMap<String,LazyFragment> mFragments = new HashMap<>();
    private NewsPagerAdapter mPagerAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.stabNewsPager);
        viewPager = (ViewPager) rootView.findViewById(R.id.vpNewsPager);
        return rootView;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        isVisible = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        if(!isPrepared || !isVisible){
            return;
        }
        initView();
        loadData();
    }

    private void initView() {

        mTitles.clear();
        mTitles.add(NewsTypes.键盘.toString());
        mTitles.add(NewsTypes.鼠标.toString());
        mTitles.add(NewsTypes.鼠垫.toString());
        mTitles.add(NewsTypes.键帽.toString());
        mTitles.add(NewsTypes.音频.toString());
        mTitles.add(NewsTypes.手柄.toString());
        mTitles.add(NewsTypes.周边.toString());
        mTitles.add(NewsTypes.游戏.toString());
        mTitles.add(NewsTypes.活动.toString());
        mTitles.add(NewsTypes.数码.toString());

        mFragments.put(NewsTypes.键盘.toString()
                ,NewsListFragment.getInStance(NewsTypes.键盘.toString()));
        mFragments.put(NewsTypes.鼠标.toString()
                ,NewsListFragment.getInStance(NewsTypes.鼠标.toString()));
        mFragments.put(NewsTypes.鼠垫.toString()
                ,NewsListFragment.getInStance(NewsTypes.鼠垫.toString()));
        mFragments.put(NewsTypes.键帽.toString()
                ,NewsListFragment.getInStance(NewsTypes.键帽.toString()));
        mFragments.put(NewsTypes.音频.toString()
                ,NewsListFragment.getInStance(NewsTypes.音频.toString()));
        mFragments.put(NewsTypes.手柄.toString()
                ,NewsListFragment.getInStance(NewsTypes.手柄.toString()));
        mFragments.put(NewsTypes.周边.toString()
                ,NewsListFragment.getInStance(NewsTypes.周边.toString()));
        mFragments.put(NewsTypes.游戏.toString()
                ,NewsListFragment.getInStance(NewsTypes.游戏.toString()));
        mFragments.put(NewsTypes.活动.toString()
                ,NewsListFragment.getInStance(NewsTypes.活动.toString()));
        mFragments.put(NewsTypes.数码.toString()
                ,NewsListFragment.getInStance(NewsTypes.数码.toString()));

        mPagerAdapter = new NewsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(mTitles.size());
        viewPager.setAdapter(mPagerAdapter);
        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }


    private class NewsPagerAdapter extends FragmentPagerAdapter{

        public NewsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(getPageTitle(position).toString());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }
    }
}
