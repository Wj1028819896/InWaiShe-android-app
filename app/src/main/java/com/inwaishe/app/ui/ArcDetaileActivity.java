package com.inwaishe.app.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.inwaishe.app.R;
import com.inwaishe.app.base.AppBarStateChangeListener;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.media.PlayerViewActivity;
import com.inwaishe.app.ui.fragment.ArcCommFragment;
import com.inwaishe.app.ui.fragment.ArcDetailFragment;
import com.inwaishe.app.viewmodel.ArcDetailViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ArcDetaileActivity extends AppCompatActivity {

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Articlelnfo mArticleInfo;
    private ImageView mIvImgBg;
    private ViewPager mVpContent;
    private SlidingTabLayout slidingTabLayout;
    private ArcDetailViewModel arcDetailViewModel;
    private LazyFragment mArcDetailFragment;
    private LazyFragment mArcCommFragment;
    private ArrayList<LazyFragment> lazyFragmentArrayList;
    private ArcDetailPagerAdapter arcDetailPagerAdapter;
    private String arcType = "";//文章类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_detaile);

        initToobar();
        initView();
        initData();
        initEvent();
    }

    private void initToobar() {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.CollapsingToolbarLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.AppBarLayout);

        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state){
                    case EXPANDED:
                        break;
                    case COLLAPSED:
                        break;
                    case IDLE:
                        break;
                }
            }
        });
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.holo_blue_dark));

    }

    private void initView() {

        mIvImgBg = (ImageView) findViewById(R.id.ivTopImgBg);
        mVpContent = (ViewPager) findViewById(R.id.vpContent);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabSlide);




    }

    private void initData() {

        mArticleInfo = (Articlelnfo) getIntent().getSerializableExtra("ARTICLE_INFO");
        arcType = mArticleInfo.arcType;
        Glide.with(this).load(mArticleInfo.artImageUrl).into(mIvImgBg);
        mCollapsingToolbarLayout.setTitle(mArticleInfo.artTitle);

        arcDetailViewModel = ViewModelProviders.of(this).get(ArcDetailViewModel.class);
        arcDetailViewModel.init(mArticleInfo);
        lazyFragmentArrayList = new ArrayList<>();
        mArcDetailFragment = new ArcDetailFragment();
        mArcCommFragment = new ArcCommFragment();
        lazyFragmentArrayList.add(mArcDetailFragment);
        lazyFragmentArrayList.add(mArcCommFragment);

        arcDetailPagerAdapter = new ArcDetailPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(arcDetailPagerAdapter);

        slidingTabLayout.setViewPager(mVpContent);

    }

    private class ArcDetailPagerAdapter extends FragmentPagerAdapter{

        private String[] mTitles = new String[]{"详情","评论"};

        public ArcDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return lazyFragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    private void initEvent() {

        mIvImgBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("视频".equals(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().arcType)
                        && !TextUtils.isEmpty(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid)){
                    Intent it = new Intent();
                    it.putExtra("AID",arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid);
                    it.setClass(ArcDetaileActivity.this,PlayerViewActivity.class);
                    ArcDetaileActivity.this.startActivity(it);
                }

            }
        });
    }
}
